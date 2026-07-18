package com.amoa.server.domain.reservation.repository;

import com.amoa.server.domain.reservation.entity.mapping.ReservationSelectedOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationSelectedOptionRepository
        extends JpaRepository<ReservationSelectedOption, Long> {

    List<ReservationSelectedOption> findAllByReservation_Id(Long reservationId);

}
