package com.ceos22.cgvclone.domain.payment.repository;

import com.ceos22.cgvclone.domain.payment.entity.Payment;
import com.ceos22.cgvclone.domain.payment.enums.PaymentStatusType;
import com.ceos22.cgvclone.domain.reservation.entity.Reservation;
import com.ceos22.cgvclone.domain.snack.entity.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservation_Uuid(UUID reservationUuid);
    Optional<Payment> findByPaymentId(String paymentId);

    Optional<Payment> findByReservation(Reservation reservation);
    Optional<Payment> findByUserOrder(UserOrder userOrder);
}
