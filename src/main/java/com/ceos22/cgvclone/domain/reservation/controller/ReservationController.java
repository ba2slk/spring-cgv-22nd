package com.ceos22.cgvclone.domain.reservation.controller;

import com.ceos22.cgvclone.domain.auth.CustomUserDetails;
import com.ceos22.cgvclone.domain.reservation.dto.ReservationCancelDTO;
import com.ceos22.cgvclone.domain.reservation.dto.ReservationRequestDTO;
import com.ceos22.cgvclone.domain.reservation.dto.ReservationResponseDTO;
import com.ceos22.cgvclone.domain.reservation.service.ReservationService;
import com.ceos22.cgvclone.domain.reservation.dto.ReservationPendingDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/api/reservations")
    public ResponseEntity<ReservationPendingDTO> createPendingReservation(@RequestBody ReservationRequestDTO reservationRequestDTO,
                                                                    @AuthenticationPrincipal CustomUserDetails user) {
        ReservationPendingDTO reservation = reservationService.createPendingReservation(reservationRequestDTO, user.getUuid());
        return ResponseEntity.ok(reservation);
    }

    @PutMapping("/api/reservations")
    public ResponseEntity<ReservationResponseDTO> cancelReservation(@RequestBody ReservationCancelDTO reservationCancelDTO,
                                                                    @AuthenticationPrincipal CustomUserDetails user) {
        ReservationResponseDTO reservation = reservationService.cancelReservation(reservationCancelDTO, user.getUuid());
        return ResponseEntity.ok(reservation);
    }
}
