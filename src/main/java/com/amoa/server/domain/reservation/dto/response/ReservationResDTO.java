package com.amoa.server.domain.reservation.dto.response;

import com.amoa.server.domain.reservation.enums.GelRemovalType;
import com.amoa.server.domain.reservation.enums.HandState;
import com.amoa.server.domain.reservation.enums.PaymentMethod;
import com.amoa.server.domain.reservation.enums.PaymentStatus;
import com.amoa.server.domain.reservation.enums.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservationResDTO {

    //예약 생성 응답 DTO
    public record CreateReservationResponse(
            Long reservationId,
            String reservationNumber,
            Long cardId,
            Long shopId,
            int totalPrice,
            Integer totalDurationMinutes,
            ReservationStatus reservationStatus

    ) {}

    public record ReservationDetailResponse(
            Long reservationId,
            String reservationNumber,

            Long cardId,
            Long shopId,
            String shopName,

            LocalDate reservationDate,
            LocalTime reservationStartTime,
            LocalTime reservationEndTime,

            String customerName,
            String customerPhoneNumber,
            String requestMessage,

            HandState handState,
            GelRemovalType gelRemovalType,
            Integer extensionRemovalCount,

            List<SelectedOptionResponse> selectedOptions,

            Integer totalPrice,
            Integer depositAmount,
            Integer totalDurationMinutes,

            ReservationStatus reservationStatus,
            PaymentStatus paymentStatus,
            PaymentMethod paymentMethod,
            Boolean refundPolicyAgreed
    ) {}

    public record SelectedOptionResponse(
            Long shopOptionId,
            String optionName,
            Integer durationMinutes,
            Integer quantity,
            Integer optionPrice,
            Integer optionTotalPrice
    ) {}
}
