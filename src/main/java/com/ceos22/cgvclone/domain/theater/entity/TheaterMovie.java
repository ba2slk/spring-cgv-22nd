package com.ceos22.cgvclone.domain.theater.entity;

import com.ceos22.cgvclone.domain.movie.entity.Movie;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
        name = "theater_movie",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"theater_id", "movie_id"})
        }
)
public class TheaterMovie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
}
