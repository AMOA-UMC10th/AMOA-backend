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
import com.amoa.server.domain.shop.repository.ShopOptionRepository;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
                request.handState(),
                request.gelRemovalType(),
                request.extensionRemovalCount()
        );

        //selectedOptions null 처리
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
                            if (!optionIds.add(optionRequest.shopOptionId())) {
                                throw new ReservationException(
                                        ReservationErrorCode.DUPLICATE_SHOP_OPTION
                                );
                            }

                            ShopOption shopOption = shopOptionRepository
                                    .findById(optionRequest.shopOptionId())
                                    .orElseThrow(() ->
                                            new ReservationException(
                                                    ReservationErrorCode.SHOP_OPTION_NOT_FOUND
                                            )
                                    );

                            validateShopOption(
                                    shopOption,
                                    shop,
                                    optionRequest.quantity()
                            );

                            return shopOption;
                        })
                        .toList();

        //가격 계산 & 시간 계산
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

            optionTotalPrice += option.getOptionPrice() * selectedOption.quantity();
            optionTotalDuration += option.getDurationMinutes() * selectedOption.quantity();
        }

        //피그마상으로 보인 기본 가격 추후 삭제 가능성 있음
        int basePrice = card.getMinPrice();
        int baseDuration = card.getDurationMinutes();

        //총 가격 & 시간
        int totalPrice = basePrice + optionTotalPrice;
        int totalDurationMinutes = baseDuration + optionTotalDuration;

        int depositAmount = calculateDepositAmount(totalPrice);

        String reservationNumber = createReservationNumber();

        Reservation reservation = ReservationConverter.toReservation(
                user,
                shop,
                card,
                request,
                reservationNumber,
                totalPrice,
                depositAmount,
                totalDurationMinutes
        );

        reservationRepository.save(reservation);

        for (int i = 0; i < optionRequests.size(); i++) {
            ReservationReqDTO.SelectedOptionRequest optionRequest =
                    optionRequests.get(i);

            ShopOption shopOption = shopOptions.get(i);

            ReservationSelectedOption selectedOption =
                    ReservationConverter.toReservationSelectedOption(
                            reservation,
                            shopOption,
                            optionRequest.quantity()
                    );

            reservationSelectedOptionRepository.save(selectedOption);
        }

        return ReservationConverter.toCreateReservationResponse(reservation);
    }

    private void validateSchedule(
            Reservation reservation,
            LocalDate reservationDate,
            LocalTime reservationStartTime,
            LocalTime reservationEndTime
    ) {
        if (reservationDate == null
                || reservationDate.isBefore(LocalDate.now())) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_RESERVATION_DATE
            );
        }

        if (reservationStartTime == null) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_RESERVATION_TIME
            );
        }

        if (reservationStartTime.isBefore(BUSINESS_OPEN_TIME)
                || reservationEndTime.isAfter(BUSINESS_CLOSE_TIME)) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_RESERVATION_TIME
            );
        }

        LocalDateTime reservationDateTime =
                LocalDateTime.of(
                        reservationDate,
                        reservationStartTime
                );

        if (reservationDateTime.isBefore(LocalDateTime.now())) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_RESERVATION_TIME
            );
        }

        List<Reservation> existingReservations =
                reservationRepository
                        .findAllByShop_IdAndReservationDate(
                                reservation.getShop().getId(),
                                reservationDate
                        );

        boolean hasConflict =
                existingReservations.stream()
                        .filter(existingReservation ->
                                !existingReservation.getId()
                                        .equals(reservation.getId())
                        )
                        .filter(existingReservation ->
                                existingReservation.getReservationStatus()
                                        != ReservationStatus.DRAFT
                        )
                        .anyMatch(existingReservation ->
                                reservationStartTime.isBefore(
                                        existingReservation
                                                .getReservationEndTime()
                                )
                                        && reservationEndTime.isAfter(
                                        existingReservation
                                                .getReservationStartTime()
                                )
                        );

        if (hasConflict) {
            throw new ReservationException(
                    ReservationErrorCode.RESERVATION_TIME_CONFLICT
            );
        }
    }

    private void validateShopOption(
            ShopOption shopOption,
            Shop shop,
            Integer quantity
    ) {
        if (!shopOption.getShop().getId().equals(shop.getId())) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_SHOP_OPTION
            );
        }

        if (!shopOption.isActive()) {
            throw new ReservationException(
                    ReservationErrorCode.INACTIVE_SHOP_OPTION
            );
        }

        if (quantity == null || quantity <= 0) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_OPTION_QUANTITY
            );
        }

        if (quantity > shopOption.getMaxQuantity()) {
            throw new ReservationException(
                    ReservationErrorCode.OPTION_QUANTITY_EXCEEDED
            );
        }
    }

    private int calculateDepositAmount(int totalPrice) {
        return totalPrice / 10;
    }

    private String createReservationNumber() {
        return "AMOA-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }

    private void validateHandStateOptions(
            HandState handState,
            GelRemovalType gelRemovalType,
            Integer extensionRemovalCount
    ) {
        if (handState != HandState.GEL_NAIL
                && gelRemovalType != null
                && gelRemovalType != GelRemovalType.NONE) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_GEL_REMOVAL_TYPE
            );
        }

        if (handState != HandState.EXTENSION_NAIL
                && extensionRemovalCount != null
                && extensionRemovalCount > 0) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_EXTENSION_REMOVAL_COUNT
            );
        }

        if (extensionRemovalCount != null) {
            if (extensionRemovalCount < 0) {
                throw new ReservationException(
                        ReservationErrorCode.INVALID_EXTENSION_REMOVAL_COUNT
                );
            }

            if (extensionRemovalCount > EXTENSION_REMOVAL_MAX_QUANTITY) {
                throw new ReservationException(
                        ReservationErrorCode.INVALID_EXTENSION_REMOVAL_COUNT
                );
            }
        }
    }

    //예약 확정 service
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
                endTime
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
                        ReservationStatus.CONFIRMED,
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
}
