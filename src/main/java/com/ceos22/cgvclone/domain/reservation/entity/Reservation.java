package com.ceos22.cgvclone.domain.reservation.entity;

import com.ceos22.cgvclone.domain.common.BaseTimeEntity;
import com.ceos22.cgvclone.domain.reservation.enums.ReservationStatusType;
import com.ceos22.cgvclone.domain.theater.entity.Showtime;
import com.ceos22.cgvclone.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Showtime showtime;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReservationStatusType status = ReservationStatusType.PENDING;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    public void cancel() {
        this.status = ReservationStatusType.CANCELED;
    }

    public void confirm() {
        this.status = ReservationStatusType.RESERVED;
    }
}
