package com.ceos22.cgvclone.domain.theater.DTO;

import com.ceos22.cgvclone.domain.theater.enums.ScreenType;

import java.util.List;

public record SeatListResponseDTO(
        Long screenId,
        String screenName,
        ScreenType screenType,
        List<SeatInfo> seats
) {
}
