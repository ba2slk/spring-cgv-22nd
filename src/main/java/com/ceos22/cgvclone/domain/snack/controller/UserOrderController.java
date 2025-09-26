package com.ceos22.cgvclone.domain.snack.controller;

import com.ceos22.cgvclone.domain.snack.dto.ItemListDTO;
import com.ceos22.cgvclone.domain.snack.dto.UserOrderRequestDTO;
import com.ceos22.cgvclone.domain.snack.dto.UserOrderResponseDTO;
import com.ceos22.cgvclone.domain.snack.service.UserOrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserOrderController {
    private final UserOrderService userOrderService;

    // 매점 상품 정보 및 이용 가능 여부 조회
    @GetMapping("/api/theaters/{theaterId}/snacks/items")
    public ResponseEntity<List<ItemListDTO>> getItems(@PathVariable Long theaterId){
        List<ItemListDTO> items = userOrderService.getItems(theaterId);
        return ResponseEntity.ok(items);
    }

    
    // 매점 상품 구매
    @PostMapping("/api/theaters/{theaterId}/snacks/orders")
    public ResponseEntity<UserOrderResponseDTO> createUserOrder(@RequestBody List<UserOrderRequestDTO> orderList,
                                                                @PathVariable Long theaterId,
                                                                @RequestParam Long userId){ // TODO: (임시) -> Spring Security
        UserOrderResponseDTO response = userOrderService.createUserOrder(orderList, theaterId, userId);
        return ResponseEntity.ok(response);
    }
}
