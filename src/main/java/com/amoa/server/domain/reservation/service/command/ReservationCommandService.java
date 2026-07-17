package com.amoa.server.domain.reservation.service.command;

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
import com.amoa.server.domain.reservation.exception.ReservationException;
import com.amoa.server.domain.reservation.exception.code.ReservationErrorCode;
import com.amoa.server.domain.reservation.repository.ReservationRepository;
import com.amoa.server.domain.reservation.repository.ReservationSelectedOptionRepository;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.entity.mapping.ShopOption;
import com.amoa.server.domain.shop.repository.ShopOptionRepository;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.user.repository.UserRepository;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
        List<ShopOption> shopOptions =
                optionRequests.stream()
                        .map(optionRequest -> {
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

        for (int i = 0; i < optionRequests.size(); i++) {
            ReservationReqDTO.SelectedOptionRequest optionRequest =
                    optionRequests.get(i);

            ShopOption shopOption = shopOptions.get(i);

            optionTotalPrice +=
                    shopOption.getOptionPrice() * optionRequest.quantity();

            optionTotalDuration +=
                    shopOption.getDurationMinutes() * optionRequest.quantity();
        }

        //피그마상으로 보인 기본 가격 추후 삭제 가능성 있음
        int basePrice = card.getMinPrice();
        int baseDuration = card.getDurationMinutes();

        //총 가격 & 시간
        int totalPrice = basePrice + optionTotalPrice;
        int totalDurationMinutes = baseDuration + optionTotalDuration;

        //종료 시간
        LocalTime reservationEndTime =
                request.reservationStartTime()
                        .plusMinutes(totalDurationMinutes);

        int depositAmount = calculateDepositAmount(totalPrice);

        String reservationNumber = createReservationNumber();

        Reservation reservation = ReservationConverter.toReservation(
                user,
                shop,
                card,
                request,
                reservationNumber,
                reservationEndTime,
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

    private void validateShopOption(
            ShopOption shopOption,
            Shop shop,
            int quantity
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
    }
}
