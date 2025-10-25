package com.ceos22.cgvclone.domain.theater.controller;

import com.ceos22.cgvclone.domain.movie.dto.MovieListDTO;
import com.ceos22.cgvclone.domain.theater.DTO.TheaterDetailsDTO;
import com.ceos22.cgvclone.domain.theater.DTO.TheaterListDTO;
import com.ceos22.cgvclone.domain.theater.service.TheaterService;
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
public class TheaterController {

    private final TheaterService theaterService;

    /* 영화관 목록 조회 */
    @GetMapping("/api/theaters")
    public ResponseEntity<List<TheaterListDTO>> getTheaters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        List<TheaterListDTO> theaters = theaterService.getTheaters(pageable);
        return ResponseEntity.ok(theaters);
    }

    /* 영화관 상세 조회 */
    @GetMapping("/api/theaters/{theaterId}")
    public ResponseEntity<TheaterDetailsDTO> getTheater(
            @PathVariable Long theaterId
    ){
        TheaterDetailsDTO theater = theaterService.getTheater(theaterId);
        return ResponseEntity.ok(theater);
    }

    /* 상영 중인 영화 목록 조회*/
    @GetMapping("/api/theaters/{theaterId}/movies")
    public ResponseEntity<List<MovieListDTO>> getMoviesFromTheater(
            @PathVariable Long theaterId
    ){
        List<MovieListDTO> movies= theaterService.getMoviesFromTheater(theaterId);
        return ResponseEntity.ok(movies);
    }

}
