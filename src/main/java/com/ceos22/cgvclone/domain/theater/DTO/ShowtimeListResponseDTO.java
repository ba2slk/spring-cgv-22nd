package com.ceos22.cgvclone.domain.theater.DTO;

import com.ceos22.cgvclone.domain.theater.entity.Showtime;
import com.ceos22.cgvclone.domain.theater.enums.ScreenType;

import java.time.LocalDateTime;
import java.util.List;

public record ShowtimeListResponseDTO(
        Long id,
        String movieTitle,
        ScreenType screenType,
        String screenName,
        int price,
        LocalDateTime startTime,
        LocalDateTime endTime
) {}