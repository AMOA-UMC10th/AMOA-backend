package com.amoa.server.domain.reservation.service.query;

import com.amoa.server.domain.reservation.converter.ReservationConverter;
import com.amoa.server.domain.reservation.dto.response.ReservationResDTO;
import com.amoa.server.domain.reservation.dto.response.ReservationResDTO.AvailableTimeResponse;
import com.amoa.server.domain.reservation.entity.Reservation;
import com.amoa.server.domain.reservation.entity.mapping.ReservationSelectedOption;
import com.amoa.server.domain.reservation.enums.ReservationStatus;
import com.amoa.server.domain.reservation.exception.ReservationException;
import com.amoa.server.domain.reservation.exception.code.ReservationErrorCode;
import com.amoa.server.domain.reservation.repository.ReservationRepository;
import com.amoa.server.domain.reservation.repository.ReservationSelectedOptionRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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

    //비즈니스 시간 고정
    private static final LocalTime BUSINESS_OPEN_TIME = LocalTime.of(10, 0);
    private static final LocalTime BUSINESS_CLOSE_TIME = LocalTime.of(20, 0);
    private static final int SLOT_INTERVAL_MINUTES = 30;

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

    public ReservationResDTO.AvailableTimesResponse getAvailableTimes(
            Long userId,
            Long reservationId,
            LocalDate reservationDate
    ) {
        validateReservationDate(reservationDate);

        Reservation reservation = reservationRepository
                .findByIdAndUser_Id(reservationId, userId)
                .orElseThrow(() ->
                        new ReservationException(
                                ReservationErrorCode.RESERVATION_NOT_FOUND
                        )
                );

        int totalDurationMinutes = reservation.getTotalDurationMinutes();

        if (totalDurationMinutes <= 0) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_RESERVATION_DURATION
            );
        }

        BusinessHours businessHours = new BusinessHours(
                BUSINESS_OPEN_TIME,
                BUSINESS_CLOSE_TIME
        );

        List<Reservation> existingReservations =
                reservationRepository.findAllByShop_IdAndReservationDateAndReservationStatusNot(
                        reservation.getShop().getId(),
                        reservationDate,
                        ReservationStatus.DRAFT
        );

        /*
         * 조회 대상 예약 자체가 같은 날짜에 저장돼 있는 경우,
         * 자기 자신 때문에 모든 시간이 겹치는 현상을 방지
         */
        existingReservations = existingReservations.stream()
                .filter(existingReservation ->
                        !existingReservation.getId().equals(reservationId)
                )
                .toList();

        int requiredSlotCount =
                (int) Math.ceil(
                        totalDurationMinutes
                                / (double) SLOT_INTERVAL_MINUTES
                );

        List<AvailableTimeResponse> availableTimes =
                createAvailableTimes(
                        reservationDate,
                        totalDurationMinutes,
                        businessHours,
                        existingReservations
                );

        return new ReservationResDTO.AvailableTimesResponse(
                reservationId,
                reservationDate,
                totalDurationMinutes,
                requiredSlotCount,
                businessHours.openingTime(),
                businessHours.closingTime(),
                availableTimes
        );
    }

    private List<AvailableTimeResponse> createAvailableTimes(
            LocalDate reservationDate,
            int totalDurationMinutes,
            BusinessHours businessHours,
            List<Reservation> existingReservations
    ) {
        List<AvailableTimeResponse> result = new ArrayList<>();

        LocalTime candidateStartTime = businessHours.openingTime();

        while (candidateStartTime.isBefore(
                businessHours.closingTime()
        )) {
            LocalTime candidateEndTime =
                    candidateStartTime.plusMinutes(
                            totalDurationMinutes
                    );

            boolean available =
                    isWithinBusinessHours(
                            candidateStartTime,
                            totalDurationMinutes,
                            businessHours.closingTime()
                    )
                            && !isPastTime(
                            reservationDate,
                            candidateStartTime
                    )
                            && !hasConflict(
                            candidateStartTime,
                            candidateEndTime,
                            existingReservations
                    );

            result.add(
                    new AvailableTimeResponse(
                            candidateStartTime,
                            available ? 1 : 0
                    )
            );

            candidateStartTime =
                    candidateStartTime.plusMinutes(
                            SLOT_INTERVAL_MINUTES
                    );
        }

        return result;
    }

    private boolean hasConflict(
            LocalTime candidateStartTime,
            LocalTime candidateEndTime,
            List<Reservation> existingReservations
    ) {
        return existingReservations.stream()
                .filter(existingReservation ->
                        existingReservation.getReservationStartTime() != null
                                && existingReservation.getReservationEndTime() != null
                )
                .anyMatch(existingReservation ->
                        isOverlapping(
                                candidateStartTime,
                                candidateEndTime,
                                existingReservation.getReservationStartTime(),
                                existingReservation.getReservationEndTime()
                        )
                );
    }

    private boolean isOverlapping(
            LocalTime candidateStartTime,
            LocalTime candidateEndTime,
            LocalTime existingStartTime,
            LocalTime existingEndTime
    ) {
        /*
         * 신규 시작 < 기존 종료
         * AND
         * 신규 종료 > 기존 시작
         *
         * 두 조건을 모두 만족하면 시간이 겹친다.
         */
        return candidateStartTime.isBefore(existingEndTime)
                && candidateEndTime.isAfter(existingStartTime);
    }

    private boolean isWithinBusinessHours(
            LocalTime candidateStartTime,
            int totalDurationMinutes,
            LocalTime businessClosingTime
    ) {
        LocalTime latestStartTime =
                businessClosingTime.minusMinutes(
                        totalDurationMinutes
                );

        return !candidateStartTime.isAfter(
                latestStartTime
        );
    }

    private boolean isPastTime(
            LocalDate reservationDate,
            LocalTime candidateStartTime
    ) {
        LocalDateTime candidateDateTime =
                LocalDateTime.of(
                        reservationDate,
                        candidateStartTime
                );

        return candidateDateTime.isBefore(
                LocalDateTime.now()
        );
    }

    private void validateReservationDate(
            LocalDate reservationDate
    ) {
        if (reservationDate == null
                || reservationDate.isBefore(LocalDate.now())) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_RESERVATION_DATE
            );
        }
    }

    private record BusinessHours(
            LocalTime openingTime,
            LocalTime closingTime
    ) {}
}