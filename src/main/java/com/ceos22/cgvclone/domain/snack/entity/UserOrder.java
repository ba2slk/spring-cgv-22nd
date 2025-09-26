package com.ceos22.cgvclone.domain.snack.entity;

import com.ceos22.cgvclone.domain.common.BaseTimeEntity;
import com.ceos22.cgvclone.domain.theater.entity.Theater;
import com.ceos22.cgvclone.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserOrder extends BaseTimeEntity {
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
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Builder.Default
    private Long totalPrice = 0L;

    public void setTotalPrice(long totalPrice) {
        if (totalPrice < 0) {
            throw new IllegalArgumentException("최종 결제 금액은 0보다 작을 수 없습니다.");
        }
        this.totalPrice = totalPrice;
    }
}
