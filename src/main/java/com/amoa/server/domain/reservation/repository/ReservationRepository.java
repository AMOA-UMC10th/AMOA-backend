package com.amoa.server.domain.reservation.repository;

import com.amoa.server.domain.reservation.entity.Reservation;
import com.amoa.server.domain.reservation.enums.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
        SELECT r
        FROM Reservation r
        WHERE r.shop.id = :shopId
          AND r.reservationDate = :reservationDate
          AND r.reservationStatus = :status
          AND r.reservationStartTime < :endTime
          AND r.reservationEndTime > :startTime
        """)
    List<Reservation> findOverlappingReservations(
            @Param("shopId") Long shopId,
            @Param("reservationDate") LocalDate reservationDate,
            @Param("status") ReservationStatus status,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    Optional<Reservation> findByIdAndUser_Id(
            Long reservationId,
            Long userId
    );

    List<Reservation>
    findAllByShop_IdAndReservationDateAndReservationStatusIn(
            Long shopId,
            LocalDate reservationDate,
            List<ReservationStatus> reservationStatus
    );

    Optional<Reservation> findByIdAndUser_IdAndReservationStatusNot(
            Long reservationId,
            Long userId,
            ReservationStatus reservationStatus
    );

    @Query("""
    SELECT r
    FROM Reservation r
    WHERE r.user.id = :userId
      AND r.reservationStatus <> :status
    ORDER BY r.reservationDate DESC,
             r.reservationStartTime DESC,
             r.id DESC
    """)
    List<Reservation> findReservationList(
            Long userId,
            ReservationStatus status,
            Pageable pageable
    );
}
