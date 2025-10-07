package com.ceos22.cgvclone.domain.theater.DTO;

import com.ceos22.cgvclone.domain.theater.entity.Theater;

public record TheaterDetailsDTO(
        Long id,
        String name,
        String location,
        String details,
        String parkingInfo,
        Boolean isOpen
) {
    public static TheaterDetailsDTO from(Theater theater) {
        return new TheaterDetailsDTO(
                theater.getId(),
                theater.getName(),
                theater.getLocation(),
                theater.getDetails(),
                theater.getParkingInfo(),
                theater.getIsOpen()
        );
    }
}
