package com.ceos22.cgvclone.domain.reservation.controller;

import com.ceos22.cgvclone.domain.reservation.dto.ReservationCancelDTO;
import com.ceos22.cgvclone.domain.reservation.dto.ReservationRequestDTO;
import com.ceos22.cgvclone.domain.reservation.dto.ReservationResponseDTO;
import com.ceos22.cgvclone.domain.reservation.repository.ReservationRepository;
import com.ceos22.cgvclone.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/api/reservation")
    public ResponseEntity<ReservationResponseDTO> createReservation(@RequestBody ReservationRequestDTO reservationRequestDTO,
                                                                     @RequestParam Long userId) {  // TODO: Spring Security 기반 User 정보 획득
        ReservationResponseDTO reservation = reservationService.createReservation(reservationRequestDTO, userId);
        return ResponseEntity.ok(reservation);
    }

    @PutMapping("/api/reservation")
    public ResponseEntity<ReservationResponseDTO> cancelReservation(@RequestBody ReservationCancelDTO reservationCancelDTO,
                                                                    @RequestParam Long userId) {  // TODO: Spring Security 기반 User 정보 획득
        ReservationResponseDTO reservation = reservationService.cancelReservation(reservationCancelDTO, userId);
        return ResponseEntity.ok(reservation);
    }
}
