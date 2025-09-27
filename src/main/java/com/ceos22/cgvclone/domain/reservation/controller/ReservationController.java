package com.ceos22.cgvclone.domain.reservation.controller;

import com.ceos22.cgvclone.domain.reservation.dto.ReservationCancelDTO;
import com.ceos22.cgvclone.domain.reservation.dto.ReservationRequestDTO;
import com.ceos22.cgvclone.domain.reservation.dto.ReservationResponseDTO;
import com.ceos22.cgvclone.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/api/reservation")
    public ResponseEntity<ReservationResponseDTO> createReservation(@RequestBody ReservationRequestDTO reservationRequestDTO,
                                                                    @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        ReservationResponseDTO reservation = reservationService.createReservation(reservationRequestDTO, user.getUsername());
        return ResponseEntity.ok(reservation);
    }

    @PutMapping("/api/reservation")
    public ResponseEntity<ReservationResponseDTO> cancelReservation(@RequestBody ReservationCancelDTO reservationCancelDTO,
                                                                    @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        ReservationResponseDTO reservation = reservationService.cancelReservation(reservationCancelDTO, user.getUsername());
        return ResponseEntity.ok(reservation);
    }
}
