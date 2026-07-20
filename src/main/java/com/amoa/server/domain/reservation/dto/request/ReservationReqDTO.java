package com.amoa.server.domain.reservation.dto.request;

import com.amoa.server.domain.reservation.enums.GelRemovalType;
import com.amoa.server.domain.reservation.enums.HandState;
import com.amoa.server.domain.reservation.enums.PaymentMethod;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public class ReservationReqDTO {

    //예약 생성 요청 DTO
    public record CreateReservationRequest(
            @NotNull(message = "카드 ID는 필수입니다.")
            Long cardId,

            @NotEmpty(message = "손 상태 선택은 필수입니다.")
            Set<HandState> handStates,
            GelRemovalType gelRemovalType,

            @Min(0)
            @Max(10)
            Integer extensionRemovalCount,
            List<SelectedOptionRequest> selectedOptions
    ) {}

    public record SelectedOptionRequest(
            @NotNull(message = "샵 옵션 ID는 필수입니다.")
            Long shopOptionId,

            @NotNull(message = "옵션 수량은 필수입니다.")
            @Min(value = 1, message = "옵션 수량은 1 이상이어야 합니다.")
            Integer quantity
    ) {}

    public record ConfirmScheduleRequest(
            @NotNull(message = "예약 날짜는 필수입니다.")
            @FutureOrPresent(message = "과거 날짜는 선택할 수 없습니다.")
            LocalDate reservationDate,

            @NotNull(message = "예약 시작 시간은 필수입니다.")
            LocalTime reservationStartTime,

            @Size(max = 500)
            String requestMessage,

            @NotNull
            PaymentMethod paymentMethod,

            @NotNull
            @AssertTrue(message = "환불 정책에 동의해야 합니다.")
            Boolean refundPolicyAgreed
    ) {
    }
}