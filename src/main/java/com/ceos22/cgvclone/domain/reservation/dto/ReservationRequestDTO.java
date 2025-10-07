package com.ceos22.cgvclone.domain.reservation.dto;

import java.util.List;

public record ReservationRequestDTO(
        Long showtimeId,
        List<Long> seatIds
) {}
