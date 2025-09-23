package com.ceos22.cgvclone.domain.theater.DTO;

import com.ceos22.cgvclone.domain.theater.entity.Theater;

public record TheaterListDTO(
        Long id,
        String name,
        String location
) {
    public static TheaterListDTO from(Theater theater) {
        return new TheaterListDTO(
                theater.getId(),
                theater.getName(),
                theater.getLocation()
        );
    }
}
