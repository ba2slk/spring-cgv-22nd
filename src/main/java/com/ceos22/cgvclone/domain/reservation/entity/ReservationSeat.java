package com.ceos22.cgvclone.domain.reservation.entity;

import com.ceos22.cgvclone.domain.reservation.key.ReservationSeatId;
import com.ceos22.cgvclone.domain.theater.entity.Seat;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "reservation_seat",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"reservation_id", "seat_id"}
                )
        }
)
public class ReservationSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    public static ReservationSeat create(Reservation reservation, Seat seat) {
        return ReservationSeat.builder()
                .reservation(reservation)
                .seat(seat)
                .build();
    }
}
