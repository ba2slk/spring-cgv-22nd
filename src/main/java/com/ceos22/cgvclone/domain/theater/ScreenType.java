package com.ceos22.cgvclone.domain.theater;

import lombok.Getter;

@Getter
public enum ScreenType {
    NORMAL("일반상영관"),
    SPECIAL("특별상영관");

    private final String description;

    private ScreenType(String description) {
        this.description = description;
    }
}
