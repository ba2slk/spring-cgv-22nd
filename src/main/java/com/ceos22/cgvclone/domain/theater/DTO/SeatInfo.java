package com.ceos22.cgvclone.domain.theater.DTO;

public record SeatInfo(
        Long seatId,
        String rowNo,
        int colNo,
        boolean isReserved
) {
}
