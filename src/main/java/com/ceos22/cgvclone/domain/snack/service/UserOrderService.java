package com.ceos22.cgvclone.domain.snack.service;

import com.ceos22.cgvclone.domain.snack.dto.ItemListDTO;
import com.ceos22.cgvclone.domain.snack.dto.UserOrderRequestDTO;
import com.ceos22.cgvclone.domain.snack.dto.UserOrderResponseDTO;
import com.ceos22.cgvclone.domain.snack.entity.Inventory;
import com.ceos22.cgvclone.domain.snack.entity.OrderItem;
import com.ceos22.cgvclone.domain.snack.entity.UserOrder;
import com.ceos22.cgvclone.domain.snack.repository.InventoryRepository;
import com.ceos22.cgvclone.domain.snack.repository.OrderItemRepository;
import com.ceos22.cgvclone.domain.snack.repository.UserOrderRepository;
import com.ceos22.cgvclone.domain.theater.repository.TheaterRepository;
import com.ceos22.cgvclone.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserOrderService {

    private final UserOrderRepository userOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final TheaterRepository theaterRepository;

    // 매점 상품 정보 및 이용 가능 여부 조회
    @Transactional(readOnly = true)
    public List<ItemListDTO> getItems(Long theaterId) {
        List<Inventory> inventories = inventoryRepository.findAllByTheaterId(theaterId);

        return inventories.stream()
                .map(inv -> ItemListDTO.from(inv.getItem(), inv.getIsAvailable(), inv.getQuantity()))
                .toList();
    }

    // 주문 정보 생성
    @Transactional
    public UserOrderResponseDTO createUserOrder(List<UserOrderRequestDTO> orderList, Long theaterId, Long userId) {  // TODO: Spring Security
        // 이용가능한 재고 목록
        List<Inventory> inventories = inventoryRepository.findAllByTheaterId(theaterId).stream()
                .filter(Inventory::getIsAvailable)
                .toList();

        // 검색 성능을 위해 매핑 (이용자가 요청한 ItemId) -> (Inventory)
        Map<Long, Inventory> inventoryMap = inventories.stream()
                .collect(Collectors.toMap(inv -> inv.getItem().getId(), inv -> inv));

        // 주문 정보 저장
        UserOrder userOrder = UserOrder.builder()
                .user(userRepository.getReferenceById(userId))
                .theater(theaterRepository.getReferenceById(theaterId))
                .totalPrice(0L)
                .build();
        userOrderRepository.save(userOrder);

        // 연결 엔터티(OrderItem) 저장
        List<OrderItem> orderItems = orderList.stream()
                .map(req -> {
                    Inventory inv = inventoryMap.get(req.itemId());
                    if (inv == null){
                        throw new IllegalArgumentException("존재하지 않는 품목입니다.");
                    }
                    if (inv.getQuantity() < req.quantity()){
                        throw new IllegalArgumentException("재고가 부족합니다.");
                    }

                    inv.setQuantity(inv.getQuantity() - req.quantity());

                    return OrderItem.builder()
                            .order(userOrder)
                            .item(inv.getItem())
                            .quantity(req.quantity())
                            .price(inv.getItem().getPrice())
                            .build();
                }).toList();

        orderItemRepository.saveAll(orderItems);

        // 최종 결제 금액 계산
        long totalPrice = orderItems.stream()
                .mapToLong(orderItem -> (long) orderItem.getPrice() * orderItem.getQuantity())
                .sum();
        userOrder.setTotalPrice(totalPrice);

        List<ItemListDTO> itemInfo = orderItems.stream()
                .map(orderItem -> ItemListDTO.from(orderItem.getItem(), true, orderItem.getQuantity()))
                .toList();

        return UserOrderResponseDTO.of(userOrder.getUuid(), itemInfo, totalPrice, theaterId);
    }
}
