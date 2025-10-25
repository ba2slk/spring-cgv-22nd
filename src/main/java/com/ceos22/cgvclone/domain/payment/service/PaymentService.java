package com.ceos22.cgvclone.domain.payment.service;

import com.ceos22.cgvclone.domain.payment.client.PaymentClient;
import com.ceos22.cgvclone.domain.payment.dto.InstantPaymentRequestDTO;
import com.ceos22.cgvclone.domain.payment.dto.InstantPaymentResponseDTO;
import com.ceos22.cgvclone.domain.reservation.entity.Reservation;
import com.ceos22.cgvclone.domain.reservation.enums.ReservationStatusType;
import com.ceos22.cgvclone.domain.reservation.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentClient paymentClient;
    private final ReservationRepository reservationRepository;

    @Value("${api.portOne.storeId}")
    private String storeId;

    private String currency = "KRW";

    @Transactional
    public InstantPaymentResponseDTO confirmPayment(
            UUID reservationUuid, String email
    ) {
        Reservation reservation = reservationRepository.findByUuid(reservationUuid)
                .orElseThrow(() -> new EntityNotFoundException("예약 정보를 찾을 수 없습니다."));

        if (!reservation.getUser().getEmail().equals(email)) {
            throw new IllegalStateException("본인의 예약만 결제할 수 있습니다.");
        }

        if (reservation.getStatus() == ReservationStatusType.RESERVED) {
            return new InstantPaymentResponseDTO(
                    reservation.getUuid().toString(),
                    Date.from(reservation.getUpdatedAt().toInstant((ZoneOffset.UTC)))
            );
        }

        if (reservation.getStatus() != ReservationStatusType.PENDING) {
            throw new IllegalStateException("결제 대기 상태의 예약이 아닙니다.");
        }

        InstantPaymentRequestDTO paymentRequest = new InstantPaymentRequestDTO(
                storeId,
                reservation.getShowtime().getMovie().getTitle(),
                reservation.getTotalPrice().intValue(),
                currency,
                null
        );

        InstantPaymentResponseDTO response = paymentClient.instant(
                reservationUuid.toString(),
                paymentRequest
        );

        reservation.confirm();
        reservationRepository.save(reservation);

        return response;
    }
}
