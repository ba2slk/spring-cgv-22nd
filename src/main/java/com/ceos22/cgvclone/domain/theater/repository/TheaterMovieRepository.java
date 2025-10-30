package com.ceos22.cgvclone.domain.theater.repository;

import com.ceos22.cgvclone.domain.movie.entity.Movie;
import com.ceos22.cgvclone.domain.theater.entity.Theater;
import com.ceos22.cgvclone.domain.theater.entity.TheaterMovie;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TheaterMovieRepository extends JpaRepository<TheaterMovie, Long> {
    @Query("SELECT tm.movie FROM TheaterMovie tm WHERE tm.theater = :theater")
    List<Movie> findAllMoviesByTheater(@Param("theater") Theater theater);

    Boolean existsByTheaterIdAndMovieId(Long theaterId, Long movieId);

}
