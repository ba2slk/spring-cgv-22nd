package com.ceos22.cgvclone.domain.movie;

import lombok.Getter;

@Getter
public enum MovieRatingType {
    ALL(0, "전체관람가"),
    TWELVE(12, "12세 이상 관람가"),
    FIFTEEN(15, "15세 이상 관람가"),
    NINETEEN(19, "19세 이상 관람가"),
    UNAVAILABLE(-1, "제한상영가"); // 관람 제한가

    private final int age;
    private final String description;

    private MovieRatingType(int age, String description) {
        this.age = age;
        this.description = description;
    }
}
