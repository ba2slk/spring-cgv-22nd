package com.ceos22.cgvclone.domain.reservation.repository;

import com.ceos22.cgvclone.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByUuid(UUID reservationUuid);
}
