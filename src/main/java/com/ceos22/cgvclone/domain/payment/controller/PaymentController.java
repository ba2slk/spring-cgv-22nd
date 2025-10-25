package com.ceos22.cgvclone.domain.payment.controller;

import com.ceos22.cgvclone.domain.payment.dto.InstantPaymentResponseDTO;
import com.ceos22.cgvclone.domain.payment.service.PaymentService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/api/payments/confirm")
    public ResponseEntity<InstantPaymentResponseDTO> confirmPayment(
            @RequestBody PaymentConfirmRequestDTO request,
            @AuthenticationPrincipal User user
    ) {
        try {
            InstantPaymentResponseDTO response = paymentService.confirmPayment(
                    request.reservationUuid(),
                    user.getUsername()
            );

            return ResponseEntity.ok(response);
        } catch (FeignException.InternalServerError e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status())
                    .body(null);
        } catch (EntityNotFoundException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    public record PaymentConfirmRequestDTO(
            UUID reservationUuid
    ){}
}
