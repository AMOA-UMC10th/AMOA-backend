package com.amoa.server.domain.reservation.converter;

import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.reservation.dto.request.ReservationReqDTO;
import com.amoa.server.domain.reservation.dto.response.ReservationResDTO;
import com.amoa.server.domain.reservation.dto.response.ReservationResDTO.SelectedOptionResponse;
import com.amoa.server.domain.reservation.entity.Reservation;
import com.amoa.server.domain.reservation.entity.mapping.ReservationSelectedOption;
import com.amoa.server.domain.reservation.enums.PaymentStatus;
import com.amoa.server.domain.reservation.enums.ReservationStatus;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.entity.mapping.ShopOption;
import com.amoa.server.domain.user.entity.User;
import java.time.LocalTime;
import java.util.List;

public final class ReservationConverter {

    private ReservationConverter() {}

    // 예약 생성 요청 → Reservation 엔티티
    public static Reservation toReservation(
            User user,
            Shop shop,
            Card card,
            ReservationReqDTO.CreateReservationRequest request,
            String reservationNumber,
            LocalTime reservationEndTime,
            int totalPrice,
            int depositAmount,
            int totalDurationMinutes
    ) {
        return Reservation.builder()
                .user(user)
                .shop(shop)
                .card(card)
                .reservationNumber(reservationNumber)
                .customerName(request.customerName())
                .customerPhoneNumber(request.customerPhoneNumber())
                .requestMessage(request.requestMessage())
                .handState(request.handState())
                .gelRemovalType(request.gelRemovalType())
                .extensionRemovalCount(
                        request.extensionRemovalCount() == null
                        ? 0
                        : request.extensionRemovalCount())
                .totalPrice(totalPrice)
                .depositAmount(depositAmount)
                .totalDurationMinutes(totalDurationMinutes)
                .paymentMethod(request.paymentMethod())
                .reservationStatus(ReservationStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .isRefundPolicyAgreed(Boolean.TRUE.equals(request.refundPolicyAgreed()))
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
                reservation.getTotalPrice(),
                reservation.getDepositAmount(),
                reservation.getTotalDurationMinutes(),
                reservation.getReservationEndTime()
        );
    }

    // Reservation + 선택 옵션 엔티티 → 예약 상세 응답 DTO
    public static ReservationResDTO.ReservationDetailResponse
    toReservationDetailResponse(
            Reservation reservation,
            List<ReservationSelectedOption> selectedOptions
    ) {
        List<SelectedOptionResponse> optionResponses =
                selectedOptions.stream()
                        .map(ReservationConverter::toSelectedOptionResponse)
                        .toList();

        return new ReservationResDTO.ReservationDetailResponse(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getCard().getId(),
                reservation.getShop().getId(),
                reservation.getShop().getShopName(),
                reservation.getReservationDate(),
                reservation.getReservationStartTime(),
                reservation.getReservationEndTime(),
                reservation.getCustomerName(),
                reservation.getCustomerPhoneNumber(),
                reservation.getRequestMessage(),
                reservation.getHandState(),
                reservation.getGelRemovalType(),
                reservation.getExtensionRemovalCount(),
                optionResponses,
                reservation.getTotalPrice(),
                reservation.getDepositAmount(),
                reservation.getTotalDurationMinutes(),
                reservation.getReservationStatus(),
                reservation.getPaymentStatus(),
                reservation.getPaymentMethod(),
                reservation.isRefundPolicyAgreed()
        );
    }

    // ReservationSelectedOption 엔티티 → 선택 옵션 응답 DTO
    private static ReservationResDTO.SelectedOptionResponse
    toSelectedOptionResponse(
            ReservationSelectedOption selectedOption
    ) {
        ShopOption shopOption = selectedOption.getShopOption();

        return new ReservationResDTO.SelectedOptionResponse(
                shopOption.getId(),
                shopOption.getOptionName(),
                selectedOption.getQuantity(),
                selectedOption.getOptionPrice(),
                selectedOption.getOptionTotalPrice(),
                shopOption.getDurationMinutes()
        );
    }
}