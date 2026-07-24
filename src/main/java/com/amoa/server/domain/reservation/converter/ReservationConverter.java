package com.amoa.server.domain.reservation.converter;

import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.reservation.dto.request.ReservationReqDTO;
import com.amoa.server.domain.reservation.dto.response.ReservationResDTO;
import com.amoa.server.domain.reservation.dto.response.ReservationResDTO.ReservationSummaryResponse;
import com.amoa.server.domain.reservation.dto.response.ReservationResDTO.SelectedOptionResponse;
import com.amoa.server.domain.reservation.entity.Reservation;
import com.amoa.server.domain.reservation.entity.mapping.ReservationSelectedOption;
import com.amoa.server.domain.reservation.enums.ReservationStatus;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.entity.mapping.ShopOption;
import com.amoa.server.domain.shop.enums.ShopOptionType;
import com.amoa.server.domain.user.entity.User;
import java.util.List;

public final class ReservationConverter {

    // 예약 생성 요청 → Reservation 엔티티
    public static Reservation toReservation(
            User user,
            Shop shop,
            Card card,
            ReservationReqDTO.CreateReservationRequest request,
            String reservationNumber,
            int totalPrice,
            int totalDurationMinutes
    ) {
        return Reservation.builder()
                .user(user)
                .shop(card.getShop())
                .card(card)
                .reservationNumber(reservationNumber)
                .customerName(user.getUserName())
                .customerPhoneNumber(user.getUserPhoneNumber())
                .handStates(request.handStates())
                .gelRemovalType(request.gelRemovalType())
                .extensionRemovalCount(request.extensionRemovalCount())
                .totalPrice(totalPrice)
                .depositAmount(shop.getDepositAmount())
                .totalDurationMinutes(totalDurationMinutes)
                .reservationStatus(ReservationStatus.DRAFT)
                .build();
    }

    //선택 옵션 요청 → ReservationSelectedOption 엔티티
    public static ReservationSelectedOption toReservationSelectedOption(
            Reservation reservation,
            ShopOption shopOption,
            int quantity
    ) {
        int optionPrice = shopOption.getOptionPrice();

        return ReservationSelectedOption.builder()
                .reservation(reservation)
                .shopOption(shopOption)
                .quantity(quantity)
                .optionPrice(shopOption.getOptionPrice())
                .optionTotalPrice(shopOption.getOptionPrice() * quantity)
                .build();
    }

    //Reservation 엔티티 → 예약 생성 응답 DTO
    public static ReservationResDTO.CreateReservationResponse
    toCreateReservationResponse(
            Reservation reservation
    ) {
        return new ReservationResDTO.CreateReservationResponse(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getCard().getId(),
                reservation.getShop().getId(),
                reservation.getHandStates(),
                reservation.getGelRemovalType(),
                reservation.getExtensionRemovalCount(),
                reservation.getTotalPrice(),
                reservation.getDepositAmount(),
                reservation.getTotalDurationMinutes(),
                reservation.getReservationStatus()
        );
    }

    //예약 확정 res
    public static ReservationResDTO.ConfirmScheduleResponse
    toConfirmScheduleResponse(
            Reservation reservation,
            String artName
    ) {
        return new ReservationResDTO.ConfirmScheduleResponse(
                reservation.getId(),
                reservation.getShop().getShopName(),
                artName,
                reservation.getReservationDate(),
                reservation.getReservationStartTime(),
                reservation.getTotalPrice()
        );
    }

    //아트 상세 조회
    public static ReservationResDTO.ReservationInfoResponse
    toReservationInfoResponse(
            Reservation reservation,
            List<ReservationSelectedOption> selectedOptions
    ) {
        String artName = selectedOptions.stream()
                .map(ReservationSelectedOption::getShopOption)
                .filter(option ->
                        option.getOptionType() == ShopOptionType.ART
                )
                .map(ShopOption::getOptionName)
                .findFirst()
                .orElse(null);

        return new ReservationResDTO.ReservationInfoResponse(
                reservation.getId(),
                reservation.getReservationStatus(),
                reservation.getShop().getShopName(),
                artName,
                reservation.getReservationDate(),
                reservation.getReservationStartTime(),
                reservation.getTotalPrice(),
                reservation.getDepositAmount(),
                reservation.getCustomerName(),
                reservation.getCustomerPhoneNumber(),
                reservation.getRequestMessage(),
                reservation.getShop().getKakaoChannelUrl()
        );
    }

    public static ReservationSummaryResponse toReservationSummaryResponse(
            Reservation reservation,
            String artName
    ) {
        return new ReservationSummaryResponse(
                reservation.getId(),
                reservation.getReservationStatus(),
                reservation.getShop().getShopName(),
                artName,
                reservation.getReservationDate(),
                reservation.getReservationStartTime(),
                reservation.getTotalPrice()
        );
    }
}