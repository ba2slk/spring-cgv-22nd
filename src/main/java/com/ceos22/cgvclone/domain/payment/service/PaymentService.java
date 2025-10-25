package com.ceos22.cgvclone.domain.payment.service;

import com.ceos22.cgvclone.domain.payment.PaymentRepository;
import com.ceos22.cgvclone.domain.payment.client.PaymentClient;
import com.ceos22.cgvclone.domain.payment.dto.InstantPaymentRequestDTO;
import com.ceos22.cgvclone.domain.payment.dto.InstantPaymentResponseDTO;
import com.ceos22.cgvclone.domain.payment.dto.PaymentDetailsResponseDTO;
import com.ceos22.cgvclone.domain.payment.entity.Payment;
import com.ceos22.cgvclone.domain.payment.enums.PaymentStatusType;
import com.ceos22.cgvclone.domain.reservation.entity.Reservation;
import com.ceos22.cgvclone.domain.reservation.enums.ReservationStatusType;
import com.ceos22.cgvclone.domain.reservation.repository.ReservationRepository;
import com.ceos22.cgvclone.domain.reservation.repository.ReservationSeatRepository;
import com.ceos22.cgvclone.domain.reservation.service.ReservationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentClient paymentClient;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final ReservationSeatRepository reservationSeatRepository;

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

        // 멱등성
        if (reservation.getStatus() == ReservationStatusType.RESERVED) {
            Payment payment = paymentRepository.findByReservation_Uuid(reservationUuid)
                    .orElseThrow(() -> new IllegalStateException("결제는 완료되었으나 결제 내역을 찾을 수 없습니다. (관리자 문의)"));

            return new InstantPaymentResponseDTO(
                    payment.getPaymentId(),
                    Date.from(payment.getPaidAt().atZone(ZoneId.systemDefault()).toInstant())
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

        LocalDateTime paidAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(response.paidAt().getTime()), // ⭐️ java.util.Date -> Instant
                ZoneId.systemDefault()
        );

        Payment payment = Payment.builder()
                .reservation(reservation)
                .paymentId(response.paymentId())
                .amount(reservation.getTotalPrice())
                .paidAt(paidAt)
                .paymentStatus(PaymentStatusType.PAID)
                .pgProvider("portOne")
                .orderName(paymentRequest.orderName())
                .currency(currency)
                .build();

        paymentRepository.save(payment);

        reservation.confirm();
        reservationRepository.save(reservation);

        return response;
    }

    @Transactional
    public PaymentDetailsResponseDTO cancelPayment(String paymentId, String email) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("결제 내역을 찾을 수 없습니다."));

        Reservation reservation = payment.getReservation();

        if (!reservation.getUser().getEmail().equals(email)) {
            throw new IllegalStateException("본인의 예약만 취소할 수 있습니다.");
        }

        if (reservation.getShowtime().getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("이미 상영 시작 시간이 지난 예매 건입니다.");
        }

        if (payment.getPaymentStatus() == PaymentStatusType.CANCELLED) {
            throw new IllegalStateException("이미 취소된 결제입니다.");
        }

        PaymentDetailsResponseDTO response = paymentClient.cancel(paymentId);

        payment.cancel();
        paymentRepository.save(payment);

        reservation.cancel();
        reservationRepository.save(reservation);

        reservationSeatRepository.deleteAllByReservation(reservation);

        return response;
    }


    @Transactional(readOnly = true)
    public PaymentDetailsResponseDTO getPaymentDetails(String paymentId, String email) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("결제 내역을 찾을 수 없습니다."));

        if (!payment.getReservation().getUser().getEmail().equals(email)) {
            throw new IllegalStateException("본인의 결제 내역만 조회할 수 있습니다.");
        }

        return paymentClient.get(paymentId);
    }

}
