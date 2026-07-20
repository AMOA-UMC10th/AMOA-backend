package com.amoa.server.domain.reservation.service.command;


import static com.amoa.server.domain.reservation.constant.ReservationOptionPolicy.BUSINESS_CLOSE_TIME;
import static com.amoa.server.domain.reservation.constant.ReservationOptionPolicy.BUSINESS_OPEN_TIME;
import static com.amoa.server.domain.reservation.constant.ReservationOptionPolicy.EXTENSION_REMOVAL_MAX_QUANTITY;

import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.card.repository.CardRepository;
import com.amoa.server.domain.reservation.converter.ReservationConverter;
import com.amoa.server.domain.reservation.dto.request.ReservationReqDTO;
import com.amoa.server.domain.reservation.dto.request.ReservationReqDTO.SelectedOptionRequest;
import com.amoa.server.domain.reservation.dto.response.ReservationResDTO;
import com.amoa.server.domain.reservation.entity.Reservation;
import com.amoa.server.domain.reservation.entity.mapping.ReservationSelectedOption;
import com.amoa.server.domain.reservation.enums.GelRemovalType;
import com.amoa.server.domain.reservation.enums.HandState;
import com.amoa.server.domain.reservation.enums.ReservationStatus;
import com.amoa.server.domain.reservation.exception.ReservationException;
import com.amoa.server.domain.reservation.exception.code.ReservationErrorCode;
import com.amoa.server.domain.reservation.repository.ReservationRepository;
import com.amoa.server.domain.reservation.repository.ReservationSelectedOptionRepository;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.entity.mapping.ShopOption;
import com.amoa.server.domain.shop.enums.ShopOptionType;
import com.amoa.server.domain.shop.repository.ShopOptionRepository;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationCommandService {
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final ShopOptionRepository shopOptionRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSelectedOptionRepository reservationSelectedOptionRepository;

    public ReservationResDTO.CreateReservationResponse createReservation(
            Long userId,
            ReservationReqDTO.CreateReservationRequest request
    ) {
        //유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ReservationException(
                                ReservationErrorCode.USER_NOT_FOUND
                        )
                );

        //카드 조회
        Card card = cardRepository.findById(request.cardId())
                .orElseThrow(() ->
                        new ReservationException(
                                ReservationErrorCode.CARD_NOT_FOUND
                        )
                );

        //샵 조회
        Shop shop = card.getShop();

        // 손 상태에 따른 제거 옵션 검증
        validateHandStateOptions(
                request.handStates(),
                request.gelRemovalType(),
                request.extensionRemovalCount()
        );

        //추가 옵션을 선택하지 않은 경우 selectedOptions null 처리
        List<SelectedOptionRequest> optionRequests =
                request.selectedOptions() == null
                        ? Collections.emptyList()
                        : request.selectedOptions();

        //shopOption 조회
        //중복 옵션 선택 방지
        Set<Long> optionIds = new HashSet<>();

        List<ShopOption> shopOptions =
                optionRequests.stream()
                        .map(optionRequest -> {

                            //같은 shopOptionId가 중복 요청된 경우 예외 처리
                            if (!optionIds.add(optionRequest.shopOptionId())) {
                                throw new ReservationException(
                                        ReservationErrorCode.DUPLICATE_SHOP_OPTION
                                );
                            }

                            //같은 shopOptionId가 중복 요청된 경우 예외 처리
                            ShopOption shopOption = shopOptionRepository
                                    .findById(optionRequest.shopOptionId())
                                    .orElseThrow(() ->
                                            new ReservationException(
                                                    ReservationErrorCode.SHOP_OPTION_NOT_FOUND
                                            )
                                    );

                            //해당 샵의 옵션인지, 활성화된 옵션인지,수량이 유효한지 검증
                            validateShopOption(
                                    shopOption,
                                    shop,
                                    optionRequest.quantity()
                            );
                            return shopOption;
                        })
                        .toList();

        //ADDITIONAL 옵션은 선택하지 않거나 여러 개 선택 가능
        //ART 옵션은 필수이며 하나만 허용
        long selectedArtCount =
                shopOptions.stream()
                        .filter(shopOption ->
                                shopOption.getOptionType()
                                        == ShopOptionType.ART
                        )
                        .count();

        if (selectedArtCount == 0) {
            throw new ReservationException(
                    ReservationErrorCode.ART_OPTION_REQUIRED
            );
        }

        if (selectedArtCount > 1) {
            throw new ReservationException(
                    ReservationErrorCode.MULTIPLE_ART_OPTIONS_NOT_ALLOWED
            );
        }

        //선택한 추가 옵션의 전체 가격과 소요 시간 계산
        int optionTotalPrice = 0;
        int optionTotalDuration = 0;

        Map<Long, ShopOption> optionMap =
                shopOptions.stream()
                        .collect(Collectors.toMap(
                                ShopOption::getId,
                                Function.identity()
                        ));

        for (ReservationReqDTO.SelectedOptionRequest selectedOption : optionRequests) {
            ShopOption option = optionMap.get(selectedOption.shopOptionId());

            //옵션 총 가격 = 옵션 단가 × 선택 수량
            optionTotalPrice +=
                    option.getOptionPrice() * selectedOption.quantity();

            //옵션 총 소요 시간 = 옵션 소요 시간 × 선택 수량
            optionTotalDuration +=
                    option.getDurationMinutes() * selectedOption.quantity();
        }

        int totalPrice = optionTotalPrice;
        int totalDurationMinutes = optionTotalDuration;

        //사용자에게 노출할 예약 번호 생성
        String reservationNumber = createReservationNumber();

        //임시 예약 엔티티 생성
        Reservation reservation =
                ReservationConverter.toReservation(
                        user,
                        shop,
                        card,
                        request,
                        reservationNumber,
                        totalPrice,
                        totalDurationMinutes
                );

        //임시 예약 저장
        Reservation savedReservation =
                reservationRepository.save(reservation);

        //사용자가 선택한 추가 옵션 저장
        for (int i = 0; i < optionRequests.size(); i++) {
            SelectedOptionRequest optionRequest =
                    optionRequests.get(i);

            ShopOption shopOption =
                    shopOptions.get(i);

            ReservationSelectedOption selectedOption =
                    ReservationConverter
                            .toReservationSelectedOption(
                                    savedReservation,
                                    shopOption,
                                    optionRequest.quantity()
                            );

            reservationSelectedOptionRepository.save(
                    selectedOption
            );
        }

        //생성된 임시 예약 정보 반환
        return ReservationConverter
                .toCreateReservationResponse(
                        savedReservation
                );
    }

    //선택한 추가 옵션의 유효성을 검증
    private void validateShopOption(
            ShopOption shopOption,
            Shop shop,
            Integer quantity
    ) {
        //선택한 옵션이 카드의 샵에 속하는지 검증
        if (!shopOption.getShop().getId()
                .equals(shop.getId())) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_SHOP_OPTION
            );
        }

        //현재 사용 가능한 옵션인지 검증
        if (!shopOption.isActive()) {
            throw new ReservationException(
                    ReservationErrorCode.INACTIVE_SHOP_OPTION
            );
        }

        // 옵션 수량이 유효한지 검증
        if (quantity == null || quantity <= 0) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_OPTION_QUANTITY
            );
        }

        // 아트 옵션은 반드시 1개만 선택 가능
        if (shopOption.getOptionType() == ShopOptionType.ART
                && quantity != 1) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_ART_OPTION_QUANTITY
            );
        }

        //샵에서 설정한 최대 선택 수량을 넘는지 검증
        if (quantity > shopOption.getMaxQuantity()) {
            throw new ReservationException(
                    ReservationErrorCode.OPTION_QUANTITY_EXCEEDED
            );
        }
    }

    //사용자에게 노출할 예약 번호를 생성
    private String createReservationNumber() {
        return "AMOA-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }

    // 손 상태에 따라 젤 제거 유형과 연장 제거 개수의 조합을 검증
    private void validateHandStateOptions(
            Set<HandState> handStates,
            GelRemovalType gelRemovalType,
            Integer extensionRemovalCount
    ) {
        //GEL_NAIL이 아닌데 젤 제거 유형을 선택한 경우
        if (!handStates.contains(HandState.GEL_NAIL)
                && gelRemovalType != null
                && gelRemovalType != GelRemovalType.NONE) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_GEL_REMOVAL_TYPE
            );
        }

        //EXTENSION_NAIL이 아닌데 연장 제거 개수를 입력한 경우
        if (!handStates.contains(HandState.EXTENSION_NAIL)
                && extensionRemovalCount != null
                && extensionRemovalCount > 0) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_EXTENSION_REMOVAL_COUNT
            );
        }

        //연장 제거 개수가 음수이거나 최대 허용 수량을 초과하는지 검증
        if (extensionRemovalCount != null) {
            if (extensionRemovalCount < 0) {
                throw new ReservationException(
                        ReservationErrorCode
                                .INVALID_EXTENSION_REMOVAL_COUNT
                );
            }

            if (extensionRemovalCount
                    > EXTENSION_REMOVAL_MAX_QUANTITY) {
                throw new ReservationException(
                        ReservationErrorCode
                                .INVALID_EXTENSION_REMOVAL_COUNT
                );
            }
        }
    }

    //예약 확정 service
    @Transactional
    public void confirmReservationSchedule(
            Long userId,
            Long reservationId,
            ReservationReqDTO.ConfirmScheduleRequest request
    ) {
        Reservation reservation = reservationRepository
                .findByIdAndUser_Id(reservationId, userId)
                .orElseThrow(() ->
                        new ReservationException(
                                ReservationErrorCode.RESERVATION_NOT_FOUND
                        )
                );

        validateDraftStatus(reservation);

        LocalDate reservationDate = request.reservationDate();
        LocalTime startTime = request.reservationStartTime();
        int totalDurationMinutes = reservation.getTotalDurationMinutes();

        validateDateAndTime(reservationDate, startTime);

        LocalTime latestStartTime =
                BUSINESS_CLOSE_TIME
                        .minusMinutes(totalDurationMinutes);

        if (startTime.isBefore(BUSINESS_OPEN_TIME)
                || startTime.isAfter(latestStartTime)) {
            throw new ReservationException(
                    ReservationErrorCode.N_SELECT_RESERVATION_TIME
            );
        }

        LocalTime endTime = startTime.plusMinutes(totalDurationMinutes);

        validateOverlap(
                reservation.getShop().getId(),
                reservationDate,
                startTime,
                endTime
        );

        reservation.confirmSchedule(
                reservationDate,
                startTime,
                endTime,
                request.requestMessage(),
                request.paymentMethod(),
                request.refundPolicyAgreed()
        );
    }

    //상태검증
    private void validateDraftStatus(Reservation reservation) {
        if (reservation.getReservationStatus()
                != ReservationStatus.DRAFT) {
            throw new ReservationException(
                    ReservationErrorCode.RESERVATION_ALREADY_CONFIRMED
            );
        }
    }

    //날짜와 현재 시간 검증
    private void validateDateAndTime(
            LocalDate reservationDate,
            LocalTime reservationStartTime
    ) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (reservationDate.isBefore(today)) {
            throw new ReservationException(
                    ReservationErrorCode.PAST_RESERVATION_DATE
            );
        }

        if (reservationDate.isEqual(today)
                && !reservationStartTime.isAfter(now)) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_RESERVATION_TIME
            );
        }
    }

    //중복 검증
    private void validateOverlap(
            Long shopId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        boolean hasConflict = !reservationRepository
                .findOverlappingReservations(
                        shopId,
                        reservationDate,
                        ReservationStatus.RESERVED,
                        startTime,
                        endTime
                )
                .isEmpty();

        if (hasConflict) {
            throw new ReservationException(
                    ReservationErrorCode.RESERVATION_TIME_CONFLICT
            );
        }
    }

    //예약 취소
    @Transactional
    public void cancelReservation(
            Long reservationId,
            Long userId
    ) {
        Reservation reservation = reservationRepository
                .findByIdAndUser_Id(reservationId, userId)
                .orElseThrow(() ->
                        new ReservationException(
                                ReservationErrorCode.RESERVATION_NOT_FOUND
                        )
                );

        validateCancelable(reservation);

        reservation.cancel();
    }

    //예약 취소 검증
    private void validateCancelable(Reservation reservation) {
        ReservationStatus status = reservation.getReservationStatus();

        if (status == ReservationStatus.CANCELED) {
            throw new ReservationException(
                    ReservationErrorCode.RESERVATION_ALREADY_CANCELLED
            );
        }

        if (status != ReservationStatus.RESERVED) {
            throw new ReservationException(
                    ReservationErrorCode.RESERVATION_CANNOT_CANCEL
            );
        }
    }
}
