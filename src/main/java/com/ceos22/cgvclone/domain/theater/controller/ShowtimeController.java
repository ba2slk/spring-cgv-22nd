package com.ceos22.cgvclone.domain.theater.controller;

import com.ceos22.cgvclone.domain.theater.DTO.SeatListResponseDTO;
import com.ceos22.cgvclone.domain.theater.DTO.ShowtimeListResponseDTO;
import com.ceos22.cgvclone.domain.theater.service.SeatService;
import com.ceos22.cgvclone.domain.theater.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShowtimeController {
    private final ShowtimeService showtimeService;
    private final SeatService seatService;

    @GetMapping("/api/showtime?theaterId={theaterId}&movieId={movieId}")
    public ResponseEntity<List<ShowtimeListResponseDTO>> getShowtimes(
            @RequestParam Long theaterId,
            @RequestParam Long movieId
    ){
        List<ShowtimeListResponseDTO> showtimeList = showtimeService.getShowtimeList(theaterId, movieId);
        return ResponseEntity.ok(showtimeList);
    }

    @GetMapping("/api/showtime/{showtimeId}/seats")
    public ResponseEntity<SeatListResponseDTO> getSeats(
            @PathVariable Long showtimeId
    ){
        SeatListResponseDTO seatList = seatService.getSeatList(showtimeId);
        return ResponseEntity.ok(seatList);
    }
}