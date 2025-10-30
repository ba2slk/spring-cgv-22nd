package com.ceos22.cgvclone.domain.snack.dto;

import com.ceos22.cgvclone.domain.snack.entity.Item;
import com.ceos22.cgvclone.domain.snack.entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record UserOrderResponseDTO(
        UUID userOrderUuid,
        List<ItemListDTO> orderedItems,
        BigDecimal totalPrice,
        Long theaterId
) {
    public static UserOrderResponseDTO of(
            UUID userOrderUuid,
            List<ItemListDTO> orderedItems,
            BigDecimal totalPrice,
            Long theaterId
    ) {
        return new UserOrderResponseDTO(
                userOrderUuid, orderedItems, totalPrice, theaterId
        );
    }
}