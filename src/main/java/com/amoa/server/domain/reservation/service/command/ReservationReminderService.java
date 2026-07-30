package com.amoa.server.domain.reservation.service.command;

import com.amoa.server.domain.reservation.entity.Reservation;
import com.amoa.server.domain.reservation.repository.ReservationRepository;
import com.amoa.server.global.sms.SmsSender;
import com.amoa.server.global.sms.SolapiScheduleClient;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationReminderService {

    private static final Duration REMINDER_BEFORE = Duration.ofHours(2);
    private static final DateTimeFormatter RESERVATION_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final SmsSender smsSender;
    private final SolapiScheduleClient solapiScheduleClient;
    private final ReservationRepository reservationRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void scheduleReminderAfterCommit(
            Long reservationId, LocalDate date, LocalTime time,
            String shopName, String reservationNumber, String customerPhoneNumber
    ) {
        LocalDateTime reservationDateTime = LocalDateTime.of(date, time);
        LocalDateTime reminderTime = reservationDateTime.minus(REMINDER_BEFORE);

        if (reminderTime.isBefore(LocalDateTime.now())) {
            return;
        }

        String startTimeText = time.format(RESERVATION_TIME_FORMATTER);
        String text = "[AMOA] " + shopName + " 예약 시간이 2시간 후인 " + startTimeText + "입니다. (예약번호 " + reservationNumber + ")";

        try {
            String groupId = smsSender.sendScheduled(customerPhoneNumber, text, reminderTime);
            reservationRepository.findById(reservationId)
                    .ifPresent(r -> r.assignReminderMessageGroupId(groupId));
        } catch (RuntimeException e) {
            log.warn("예약 리마인드 문자 예약 실패. reservationId={}", reservationId, e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cancelReminderAfterCommit(Long reservationId, String groupId) {
        try {
            solapiScheduleClient.cancelSchedule(groupId);
            reservationRepository.findById(reservationId)
                    .ifPresent(Reservation::clearReminderMessageGroupId);
        } catch (RuntimeException e) {
            log.warn("예약 리마인드 문자 취소 실패. reservationId={}", reservationId, e);
        }
    }
}