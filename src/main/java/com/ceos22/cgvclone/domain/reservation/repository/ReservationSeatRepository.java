package com.ceos22.cgvclone.domain.reservation.repository;

import com.ceos22.cgvclone.domain.reservation.entity.Reservation;
import com.ceos22.cgvclone.domain.reservation.entity.ReservationSeat;
import com.ceos22.cgvclone.domain.theater.entity.Seat;
import com.ceos22.cgvclone.domain.theater.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
    boolean existsByReservationShowtimeAndSeatIn(Showtime showtime, List<Seat> seats);
    void deleteAllByReservation(Reservation reservation);
}
