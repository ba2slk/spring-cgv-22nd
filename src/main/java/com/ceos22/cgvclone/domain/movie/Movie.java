package com.ceos22.cgvclone.domain.movie;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private int duration;

    private LocalDate releaseDate;
    private LocalDate closeDate;

    private String director;

    @Builder.Default
    private Long totalView = 0L;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MovieRatingType rating = MovieRatingType.ALL;

    @Builder.Default
    private Double star = 0.0;
}
