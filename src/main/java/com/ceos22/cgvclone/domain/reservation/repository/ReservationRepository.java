package com.ceos22.cgvclone.domain.reservation.repository;

import com.ceos22.cgvclone.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
