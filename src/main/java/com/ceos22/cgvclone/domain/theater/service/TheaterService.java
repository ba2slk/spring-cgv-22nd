package com.ceos22.cgvclone.domain.theater.service;

import com.ceos22.cgvclone.domain.movie.dto.MovieListDTO;
import com.ceos22.cgvclone.domain.movie.entity.Movie;
import com.ceos22.cgvclone.domain.theater.DTO.ShowtimeListResponseDTO;
import com.ceos22.cgvclone.domain.theater.DTO.TheaterDetailsDTO;
import com.ceos22.cgvclone.domain.theater.DTO.TheaterListDTO;
import com.ceos22.cgvclone.domain.theater.entity.Screen;
import com.ceos22.cgvclone.domain.theater.entity.Showtime;
import com.ceos22.cgvclone.domain.theater.entity.Theater;
import com.ceos22.cgvclone.domain.theater.repository.ShowtimeRepository;
import com.ceos22.cgvclone.domain.theater.repository.TheaterMovieRepository;
import com.ceos22.cgvclone.domain.theater.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TheaterService {

    private final TheaterRepository theaterRepository;
    private final TheaterMovieRepository theaterMovieRepository;
    private final ShowtimeRepository showtimeRepository;

    /* 영화관 목록 조회 */
    @Transactional(readOnly = true)
    public List<TheaterListDTO> getTheaters(Pageable pageable) {
        Page<Theater> theaters = theaterRepository.findAll(pageable);
        return theaters.stream()
                .map(TheaterListDTO::from)
                .collect(Collectors.toList());
    }

    /* 영화관 상세 조회 */
    @Transactional(readOnly = true)
    public TheaterDetailsDTO getTheater(Long theaterId) {
        Optional<Theater> Theater = theaterRepository.findById(theaterId);
        return Theater.map(TheaterDetailsDTO::from).orElseThrow();
    }

    /* 영화관 별 영화 목록 조회 */
    @Transactional(readOnly = true)
    public List<MovieListDTO> getMoviesFromTheater(Long theaterId) {

        List<Movie> movieList = getMovieList(theaterId);

        return movieList.stream()
                .map(movie -> new MovieListDTO(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getDuration(),
                        movie.getDirector(),
                        movie.getRating(),
                        movie.getStar()
                ))
                .toList();
    }

    private List<Movie> getMovieList(Long theaterId) {
        return theaterRepository.findById(theaterId)
                .map(theaterMovieRepository::findAllMoviesByTheater)
                .orElse(Collections.emptyList());
    }
}
