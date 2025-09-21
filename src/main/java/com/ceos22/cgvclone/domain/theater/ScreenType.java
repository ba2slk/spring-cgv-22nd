package com.ceos22.cgvclone.domain.theater;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScreenType {
    NORMAL("일반상영관"),
    SPECIAL("특별상영관");

    private final String description;
}
