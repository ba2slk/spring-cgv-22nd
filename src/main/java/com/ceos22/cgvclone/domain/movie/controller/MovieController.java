package com.ceos22.cgvclone.domain.movie.controller;

import com.ceos22.cgvclone.domain.movie.dto.MovieDetailsDTO;
import com.ceos22.cgvclone.domain.movie.dto.MovieListDTO;
import com.ceos22.cgvclone.domain.movie.enums.MovieStatusType;
import com.ceos22.cgvclone.domain.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    /* 영화 목록 조회 */
    @GetMapping("api/movies")
    public ResponseEntity<List<MovieListDTO>> getMovies(
            @RequestParam(defaultValue = "ONSCREEN") MovieStatusType status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "releaseDate") String sortBy
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        List<MovieListDTO> movies = movieService.getMovies(status, pageable);
        return ResponseEntity.ok(movies);
    }

    /* 영화 상세 조회 */
    @GetMapping("api/movie/{movieId}")
    public ResponseEntity<MovieDetailsDTO> getMovie(
            @PathVariable Long movieId
    ){
        MovieDetailsDTO movie = movieService.getMovie(movieId);
        return ResponseEntity.ok(movie);
    }
}

