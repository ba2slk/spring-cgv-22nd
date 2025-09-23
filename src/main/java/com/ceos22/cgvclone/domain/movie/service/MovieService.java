package com.ceos22.cgvclone.domain.movie.service;

import com.ceos22.cgvclone.domain.movie.dto.MovieDetailsDTO;
import com.ceos22.cgvclone.domain.movie.dto.MovieListDTO;
import com.ceos22.cgvclone.domain.movie.entity.Movie;
import com.ceos22.cgvclone.domain.movie.enums.MovieStatusType;
import com.ceos22.cgvclone.domain.movie.repository.MovieRepository;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    /* MovieStatusType 기반 영화 목록 조회 */
    @Transactional(readOnly = true)
    public List<MovieListDTO> getMovies(MovieStatusType status, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByStatus(status, pageable);
        return movies.stream()
                .map(MovieListDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MovieDetailsDTO getMovie(Long movieId){
        Optional<Movie> movie = movieRepository.findById(movieId);
        return movie.map(MovieDetailsDTO::from).orElseThrow();
    }
}
