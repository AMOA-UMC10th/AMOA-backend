package com.amoa.server.domain.reservation.service.query;

import com.amoa.server.domain.reservation.converter.ReservationConverter;
import com.amoa.server.domain.reservation.dto.response.ReservationResDTO;
import com.amoa.server.domain.reservation.entity.Reservation;
import com.amoa.server.domain.reservation.entity.mapping.ReservationSelectedOption;
import com.amoa.server.domain.reservation.exception.ReservationException;
import com.amoa.server.domain.reservation.exception.code.ReservationErrorCode;
import com.amoa.server.domain.reservation.repository.ReservationRepository;
import com.amoa.server.domain.reservation.repository.ReservationSelectedOptionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryService {

    private final ReservationRepository reservationRepository;
    private final ReservationSelectedOptionRepository reservationSelectedOptionRepository;

    public ReservationResDTO.ReservationDetailResponse getReservationDetail(
            Long userId,
            Long reservationId
    ) {
        Reservation reservation = reservationRepository
                .findByIdAndUser_Id(reservationId, userId)
                .orElseThrow(() ->
                        new ReservationException(
                                ReservationErrorCode.RESERVATION_NOT_FOUND
                        )
                );

        List<ReservationSelectedOption> selectedOptions =
                reservationSelectedOptionRepository
                        .findAllByReservation_Id(reservationId);

        return ReservationConverter.toReservationDetailResponse(
                reservation,
                selectedOptions
        );
    }
}