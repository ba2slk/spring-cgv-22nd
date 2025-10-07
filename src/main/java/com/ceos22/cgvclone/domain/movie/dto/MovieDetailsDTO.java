package com.ceos22.cgvclone.domain.movie.dto;

import com.ceos22.cgvclone.domain.movie.entity.Movie;
import com.ceos22.cgvclone.domain.movie.enums.MovieRatingType;

import java.time.LocalDate;

public record MovieDetailsDTO(
        Long id,
        String title,
        Integer duration,
        LocalDate releaseDate,
        String director,
        Long totalView,
        MovieRatingType rating,
        Double star
) {
    public static MovieDetailsDTO from(Movie movie) {
        return new MovieDetailsDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getDuration(),
                movie.getReleaseDate(),
                movie.getDirector(),
                movie.getTotalView(),
                movie.getRating(),
                movie.getStar()
        );
    }
}
