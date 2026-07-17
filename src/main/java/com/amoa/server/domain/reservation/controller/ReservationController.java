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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController implements ReservationControllerDocs {

    private final ReservationCommandService reservationCommandService;
    private final ReservationQueryService reservationQueryService;

    @Override
    @PostMapping
    public ApiResponse<ReservationResDTO.CreateReservationResponse> createReservation(
            @Valid @RequestBody ReservationReqDTO.CreateReservationRequest request,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Long userId = principal.user().getId();

        return ApiResponse.onSuccess(
                ReservationSuccessCode.RESERVATION_OPTIONS_FOUND,
                reservationCommandService.createReservation(userId, request)
        );
    }

    @Override
    @GetMapping("/{reservationId}")
    public ApiResponse<ReservationResDTO.ReservationDetailResponse> getReservationDetail(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Long userId = principal.user().getId();

        return ApiResponse.onSuccess(
                ReservationSuccessCode.RESERVATION_OPTIONS_FOUND,
                reservationQueryService.getReservationDetail(userId, reservationId)
        );
    }
}
