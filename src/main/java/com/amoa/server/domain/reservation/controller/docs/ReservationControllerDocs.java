package com.amoa.server.domain.reservation.controller.docs;

import com.amoa.server.domain.reservation.dto.request.ReservationReqDTO;
import com.amoa.server.domain.reservation.dto.response.ReservationResDTO;
import com.amoa.server.global.apiPayload.ApiResponse;
import com.amoa.server.global.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Reservation", description = "예약 관련 API")
public interface ReservationControllerDocs {

    @Operation(
            summary = "예약 생성",
            description = "사용자가 아트 카드와 추가 옵션을 선택하여 예약을 생성합니다."
    )
    ApiResponse<ReservationResDTO.CreateReservationResponse> createReservation(
            ReservationReqDTO.CreateReservationRequest request,

            @Parameter(hidden = true)
            CustomUserDetails principal
    );

    @Operation(
            summary = "예약 가능 시간 조회 API",
            description = "예약 날짜를 기준으로 선택 가능한 예약 시간을 조회합니다."
    )
    ApiResponse<ReservationResDTO.AvailableTimesResponse> getAvailableTimes(
            @Parameter(description = "예약 ID", example = "1")
            @PathVariable Long reservationId,

            @Parameter(description = "예약 날짜", example = "2026-07-20")
            @RequestParam LocalDate date,

            @AuthenticationPrincipal CustomUserDetails principal
    );
}
