package com.ceos22.cgvclone.domain.snack.repository;

import com.ceos22.cgvclone.domain.snack.entity.OrderItem;
import com.ceos22.cgvclone.domain.snack.entity.UserOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @EntityGraph(attributePaths = {"item"})
    List<OrderItem> findByOrder(UserOrder userOrder); // 주문 취소 시 재고 복구 용도
}
