package com.ceos22.cgvclone.domain.reservation.dto;

import java.util.List;
import java.util.UUID;

public record ReservationResponseDTO(
        UUID reservationUuid,
        Long showtimeId,
        List<Long> seatIds,
        String status
) {}
