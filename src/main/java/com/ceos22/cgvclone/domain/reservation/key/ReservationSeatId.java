package com.ceos22.cgvclone.domain.reservation.key;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ReservationSeatId implements Serializable {
    private Long reservation;
    private Long seat;
}
