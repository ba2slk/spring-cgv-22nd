package com.ceos22.cgvclone.domain.snack.entity;

import com.ceos22.cgvclone.domain.theater.entity.Theater;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"theater_id", "item_id"})
        }
)
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private Boolean isAvailable = true;

    @Builder.Default
    private int quantity = 0;

    // 재고 수량 조정
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalStateException("재고는 0보다 작을 수 없습니다.");
        }
        this.quantity = quantity;
    }
}
