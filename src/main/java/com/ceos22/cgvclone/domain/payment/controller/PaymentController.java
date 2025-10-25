package com.ceos22.cgvclone.domain.payment.controller;

import com.ceos22.cgvclone.domain.payment.dto.InstantPaymentResponseDTO;
import com.ceos22.cgvclone.domain.payment.dto.PaymentDetailsResponseDTO;
import com.ceos22.cgvclone.domain.payment.service.PaymentService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
            @AuthenticationPrincipal User user
    ) {
        try {
            InstantPaymentResponseDTO response = paymentService.confirmPayment(
                    request.payableUuid,
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
            UUID payableUuid
    ){}

    /* 결제 취소 */
    @PostMapping("/api/payments/cancel")
    public ResponseEntity<PaymentDetailsResponseDTO> cancelPayment(
            @RequestBody CancelRequestDTO request,
            @AuthenticationPrincipal User user
    ) {
        try {
            PaymentDetailsResponseDTO response = paymentService.cancelPayment(
                    request.paymentId(),
                    user.getUsername()
            );
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException | IllegalStateException e) {
            // (내역 없음, 본인 아님, 이미 취소됨, 상영 시간 지남 등)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);

        } catch (FeignException e) {
            // (Mock 서버 취소 실패)
            return ResponseEntity.status(e.status())
                    .body(null);
        }
    }

    public record CancelRequestDTO(
            String paymentId
    ) {}

    /* 결제 정보 단건 조회 */
    @GetMapping("/api/payments")
    public ResponseEntity<PaymentDetailsResponseDTO> get(
            @RequestParam("paymentId") String paymentId,
            @AuthenticationPrincipal User user
    ) {
        try {
            PaymentDetailsResponseDTO response = paymentService.getPaymentDetails(
                    paymentId, user.getUsername()
            );
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status())
                    .body(null);
        }

    }
}
