package com.amoa.server.domain.reservation.dto.request;

import com.amoa.server.domain.reservation.dto.response.ReservationResDTO.SelectedOptionResponse;
import com.amoa.server.domain.reservation.enums.GelRemovalType;
import com.amoa.server.domain.reservation.enums.HandState;
import com.amoa.server.domain.reservation.enums.PaymentMethod;
import com.amoa.server.domain.reservation.enums.PaymentStatus;
import com.amoa.server.domain.reservation.enums.ReservationStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservationReqDTO {

    //예약 생성 요청 DTO
    public record CreateReservationRequest(
            Long cardId,
            String customerName,
            String customerPhoneNumber,
            String requestMessage,
            HandState handState,
            GelRemovalType gelRemovalType,
            @Min(0)
            @Max(10)
            Integer extensionRemovalCount,
            List<SelectedOptionRequest> selectedOptions,
            PaymentMethod paymentMethod,
            Boolean refundPolicyAgreed
    ) {}

    public record SelectedOptionRequest(
            Long shopOptionId,
            Integer quantity
    ) {}

    public record AvailableTimeRequest(
            Long cardId,
            LocalDate reservationDate,
            HandState handState,
            GelRemovalType gelRemovalType,
            Integer extensionRemovalCount,
            List<SelectedOptionRequest> selectedOptions
    ) {}

    public record ConfirmReservationScheduleRequest(

            @NotNull
            @FutureOrPresent
            LocalDate reservationDate,

            @NotNull
            LocalTime reservationStartTime
    ) {}
}