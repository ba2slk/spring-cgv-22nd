package com.ceos22.cgvclone.domain.reservation.dto;

import java.util.UUID;

public record ReservationUpdateDTO(
        UUID uuid,
        String status
) {
}
