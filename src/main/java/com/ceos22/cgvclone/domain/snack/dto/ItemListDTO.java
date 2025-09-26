package com.ceos22.cgvclone.domain.snack.dto;

import com.ceos22.cgvclone.domain.snack.entity.Item;

// 매점 상품 정보 및 이용 가능 여부
public record ItemListDTO(
        Long itemId,
        String itemName,
        int price,
        String image,
        Boolean isAvailable,
        int quantity
) {
    public static ItemListDTO from(Item item, Boolean isAvailable, int quantity) {
        return new ItemListDTO(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getImage(),
                isAvailable,
                quantity
        );
    }
}
