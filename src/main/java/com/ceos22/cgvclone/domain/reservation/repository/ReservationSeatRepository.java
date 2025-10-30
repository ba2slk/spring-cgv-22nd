package com.ceos22.cgvclone.domain.reservation.repository;

import com.ceos22.cgvclone.domain.reservation.entity.Reservation;
import com.ceos22.cgvclone.domain.reservation.entity.ReservationSeat;
import com.ceos22.cgvclone.domain.theater.entity.Seat;
import com.ceos22.cgvclone.domain.theater.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
    boolean existsByReservationShowtimeAndSeatIn(Showtime showtime, List<Seat> seats);
    void deleteAllByReservation(Reservation  reservation);

    @Query("SELECT rs.seat.id FROM ReservationSeat rs " +
            "WHERE rs.reservation.showtime.id = :showtimeId")
    List<Long> findReservedSeatIdsByShowtimeId(@Param("showtimeId") Long showtimeId);

    @Query("SELECT COUNT(rs) > 0 FROM ReservationSeat rs " +
            "WHERE rs.reservation.showtime.id = :showtimeId " +
            "AND rs.seat.id IN :seatIds " +
            "AND rs.reservation.status != 'CANCELED'")
    boolean existsActiveReservationForSeats(
            @Param("showtimeId") Long showtimeId,
            @Param("seatIds") List<Long> seatIds
    );
}
