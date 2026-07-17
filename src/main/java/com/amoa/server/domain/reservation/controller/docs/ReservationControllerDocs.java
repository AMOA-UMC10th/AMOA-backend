package com.amoa.server.domain.reservation.controller.docs;

import com.amoa.server.domain.reservation.dto.request.ReservationReqDTO;
import com.amoa.server.domain.reservation.dto.response.ReservationResDTO;
import com.amoa.server.global.apiPayload.ApiResponse;
import com.amoa.server.global.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

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
            summary = "예약 옵션 조회",
            description = "예약 ID를 기준으로 예약 정보와 사용자가 선택한 옵션을 조회합니다."
    )
    ApiResponse<ReservationResDTO.ReservationDetailResponse> getReservationDetail(
            Long reservationId,

            @Parameter(hidden = true)
            CustomUserDetails principal
    );
}
