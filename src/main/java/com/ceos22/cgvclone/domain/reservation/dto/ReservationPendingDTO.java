package com.ceos22.cgvclone.domain.reservation.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ReservationPendingDTO(
        UUID uuid,
        String movieTitle,
        String screenName,
        BigDecimal totalPrice,
        String status
) {
}
