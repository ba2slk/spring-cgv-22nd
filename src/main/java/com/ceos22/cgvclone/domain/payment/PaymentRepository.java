package com.ceos22.cgvclone.domain.payment;

import com.ceos22.cgvclone.domain.payment.entity.Payment;
import com.ceos22.cgvclone.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservation_Uuid(UUID reservationUuid);
    Optional<Payment> findByPaymentId(String paymentId);
}
