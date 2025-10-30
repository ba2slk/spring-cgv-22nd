package com.ceos22.cgvclone.domain.payment.entity;

import com.ceos22.cgvclone.domain.payment.enums.PaymentStatusType;
import com.ceos22.cgvclone.domain.reservation.entity.Reservation;
import com.ceos22.cgvclone.domain.snack.entity.UserOrder;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // 티켓 예매
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", unique = true)
    Reservation reservation;

    // 매점 주문
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_order_id", unique = true)
    private UserOrder userOrder;

    @Column(nullable = false, unique = true)
    String paymentId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    PaymentStatusType paymentStatus;

    @Column(nullable = false)
    BigDecimal amount;

    @Column(nullable = false)
    String orderName;

    @Column(nullable = false)
    String pgProvider;

    @Column(nullable = false)
    String currency;

    String customData;

    @Column(nullable = false)
    LocalDateTime paidAt;

    LocalDateTime canceledAt;

    public void cancel(){
        this.paymentStatus = PaymentStatusType.CANCELED;
    }

    public Payable getPayable() {
        return (reservation != null) ? reservation : userOrder;
    }
}
