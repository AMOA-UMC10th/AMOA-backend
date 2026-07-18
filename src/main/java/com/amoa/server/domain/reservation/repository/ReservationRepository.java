package com.amoa.server.domain.reservation.repository;

import com.amoa.server.domain.reservation.entity.Reservation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByIdAndUser_Id(Long reservationId, Long userId);
}
