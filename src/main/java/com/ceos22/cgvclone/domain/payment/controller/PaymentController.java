package com.ceos22.cgvclone.domain.payment.controller;

import com.ceos22.cgvclone.domain.auth.CustomUserDetails;
import com.ceos22.cgvclone.domain.payment.dto.InstantPaymentResponseDTO;
import com.ceos22.cgvclone.domain.payment.dto.PaymentDetailsResponseDTO;
import com.ceos22.cgvclone.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    /* 결제 */
    @PostMapping("/api/payments/confirm")
    public ResponseEntity<InstantPaymentResponseDTO> confirmPayment(
            @RequestBody PaymentConfirmRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        InstantPaymentResponseDTO response = paymentService.confirmPayment(
                request.payableUuid,
                user.getUsername()
        );
        return ResponseEntity.ok(response);

    }

    /* 결제 취소 */
    @PostMapping("/api/payments/cancel")
    public ResponseEntity<PaymentDetailsResponseDTO> cancelPayment(
            @RequestBody CancelRequestDTO request,
            @AuthenticationPrincipal User user
    ) {
        PaymentDetailsResponseDTO response = paymentService.cancelPayment(
                request.paymentId(),
                user.getUsername()
        );
        return ResponseEntity.ok(response);

    }

    /* 결제 정보 단건 조회 */
    @GetMapping("/api/payments")
    public ResponseEntity<PaymentDetailsResponseDTO> get(
            @RequestParam("paymentId") String paymentId,
            @AuthenticationPrincipal User user
    ) {
        PaymentDetailsResponseDTO response = paymentService.getPaymentDetails(
                paymentId, user.getUsername()
        );
        return ResponseEntity.ok(response);
    }


    /* 요청 DTO */
    public record PaymentConfirmRequestDTO(
            UUID payableUuid
    ){}

    public record CancelRequestDTO(
            String paymentId
    ) {}
}
