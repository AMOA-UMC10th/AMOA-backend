package com.amoa.server.domain.reservation.controller;

import com.amoa.server.domain.reservation.controller.docs.ReservationControllerDocs;
import com.amoa.server.domain.reservation.dto.request.ReservationReqDTO;
import com.amoa.server.domain.reservation.dto.response.ReservationResDTO;
import com.amoa.server.domain.reservation.exception.code.ReservationSuccessCode;
import com.amoa.server.domain.reservation.service.command.ReservationCommandService;
import com.amoa.server.domain.reservation.service.query.ReservationQueryService;
import com.amoa.server.global.apiPayload.ApiResponse;
import com.amoa.server.global.auth.CustomUserDetails;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController implements ReservationControllerDocs {

    private final ReservationCommandService reservationCommandService;
    private final ReservationQueryService reservationQueryService;

    //예약 최초 생성
    @Override
    @PostMapping("/schedule")
    public ApiResponse<ReservationResDTO.CreateReservationResponse> createReservation(
            @Valid @RequestBody ReservationReqDTO.CreateReservationRequest request,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Long userId = principal.user().getId();

        return ApiResponse.onSuccess(
                ReservationSuccessCode.RESERVATION_CREATED,
                reservationCommandService.createReservation(userId, request)
        );
    }

    //예약 최종 생성
    @Override
    @PatchMapping("/{reservationId}/confirm")
    public ApiResponse<ReservationResDTO.ConfirmScheduleResponse> confirmReservationSchedule(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long reservationId,
            @Valid @RequestBody
            ReservationReqDTO.ConfirmScheduleRequest request
    ) {
        ReservationResDTO.ConfirmScheduleResponse response =
                reservationCommandService.confirmReservationSchedule(
                        principal.user().getId(),
                        reservationId,
                        request
                );

        return ApiResponse.onSuccess(
                ReservationSuccessCode.RESERVATION_SCHEDULE_CONFIRMED,
                response
        );
    }

    //예약 가능 시간 조회
    @Override
    @GetMapping("/{reservationId}/available-times")
    public ApiResponse<ReservationResDTO.AvailableTimesResponse>
    getAvailableTimes(
            @PathVariable Long reservationId,
            @RequestParam LocalDate date,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ApiResponse.onSuccess(
                ReservationSuccessCode.RESERVATION_AVAILABLE_TIMES_FOUND,
                reservationQueryService.getAvailableTimes(
                        principal.user().getId(),
                        reservationId,
                        date
                )
        );
    }

    //예약 상세 조회
    @Override
    @GetMapping("/{reservationId}")
    public ApiResponse<ReservationResDTO.ReservationInfoResponse>
    getReservationInfo(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long reservationId
    ) {
        return ApiResponse.onSuccess(
                ReservationSuccessCode.RESERVATION_DETAIL_SUCCESS,
                reservationQueryService.getReservationInfo(
                        reservationId,
                        principal.user().getId()
                )
        );
    }

    //예약 목록 조회
    @Override
    @GetMapping
    public ApiResponse<ReservationResDTO.ReservationListResponse> getReservationList(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.onSuccess(
                ReservationSuccessCode.RESERVATION_LIST_OK,
                reservationQueryService.getReservationList(
                        principal.user().getId(),
                        size
                )
        );
    }

    //예약 취소
    @Override
    @PatchMapping("/{reservationId}/cancel")
    public ApiResponse<Void> cancelReservation(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long reservationId
    ) {
        reservationCommandService.cancelReservation(
                reservationId,
                principal.user().getId()
        );

        return ApiResponse.onSuccess(
                ReservationSuccessCode.RESERVATION_CANCEL_OK,
                null
        );
    }
}
