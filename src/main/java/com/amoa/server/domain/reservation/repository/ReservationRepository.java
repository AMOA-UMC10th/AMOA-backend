package com.amoa.server.domain.reservation.repository;

import com.amoa.server.domain.reservation.entity.Reservation;
import com.amoa.server.domain.reservation.enums.ReservationStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByIdAndUser_Id(
            Long reservationId,
            Long userId
    );

    List<Reservation> findAllByShop_IdAndReservationDate(
            Long shopId,
            LocalDate reservationDate
    );

    List<Reservation>
    findAllByShop_IdAndReservationDateAndReservationStatusNot(
            Long shopId,
            LocalDate reservationDate,
            ReservationStatus reservationStatus
    );
}
