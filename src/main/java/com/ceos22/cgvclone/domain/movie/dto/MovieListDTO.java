package com.ceos22.cgvclone.domain.movie.dto;

import com.ceos22.cgvclone.domain.movie.entity.Movie;
import com.ceos22.cgvclone.domain.movie.enums.MovieRatingType;

public record MovieListDTO(
        Long id,
        String title,
        Integer duration,
        String director,
        MovieRatingType rating,
        Double star
) {
    public static MovieListDTO from(Movie movie) {
        return new MovieListDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getDuration(),
                movie.getDirector(),
                movie.getRating(),
                movie.getStar()
        );
    }
}
