package com.ceos22.cgvclone.domain.snack.dto;

import com.ceos22.cgvclone.domain.snack.entity.Inventory;
import com.ceos22.cgvclone.domain.snack.entity.Item;
import com.ceos22.cgvclone.domain.snack.entity.OrderItem;

// 매점 상품 정보 및 이용 가능 여부
public record ItemListDTO(
        Long itemId,
        String itemName,
        int price,
        String image,
        Boolean isAvailable,
        int quantity  // 1. 재고 수량  2. 주문 수량
) {
    public static ItemListDTO fromInventory(Inventory inventory) {
        return new ItemListDTO(
                inventory.getItem().getId(),
                inventory.getItem().getName(),
                inventory.getItem().getPrice(),
                inventory.getItem().getImage(),
                inventory.getIsAvailable(),
                inventory.getQuantity()
        );
    }

    public static ItemListDTO fromOrderItem(OrderItem orderItem){
        return new ItemListDTO(
                orderItem.getItem().getId(),
                orderItem.getItem().getName(),
                orderItem.getPrice(),
                orderItem.getItem().getImage(),
                true,
                orderItem.getQuantity()
        );
    }
}
