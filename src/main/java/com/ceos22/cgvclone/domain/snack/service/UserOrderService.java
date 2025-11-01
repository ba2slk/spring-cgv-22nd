package com.ceos22.cgvclone.domain.snack.service;

import com.ceos22.cgvclone.domain.payment.enums.PaymentStatusType;
import com.ceos22.cgvclone.domain.snack.UserOrderStatusType;
import com.ceos22.cgvclone.domain.snack.dto.ItemListDTO;
import com.ceos22.cgvclone.domain.snack.dto.UserOrderRequestDTO;
import com.ceos22.cgvclone.domain.snack.dto.UserOrderResponseDTO;
import com.ceos22.cgvclone.domain.snack.entity.Inventory;
import com.ceos22.cgvclone.domain.snack.entity.Item;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
                .map(ItemListDTO::fromInventory)
                .toList();
    }

    // 주문 정보 생성
    @Transactional
    public UserOrderResponseDTO createPendingUserOrder(
            List<UserOrderRequestDTO> orderList, Long theaterId, UUID userUuid
    ) {

        List<Long> requestedItemIds = orderList.stream()
                .map(UserOrderRequestDTO::itemId)
                .toList();

        List<Inventory> lockedInventories = inventoryRepository.findByTheaterIdAndItemIdInWithLock(theaterId, requestedItemIds);

        Map<Long, Inventory> inventoryMap = lockedInventories.stream()
                .collect(Collectors.toMap(inv -> inv.getItem().getId(), inv -> inv));

        UserOrder userOrder = UserOrder.builder()
                .user(userRepository.getReferenceByUuid(userUuid))
                .theater(theaterRepository.getReferenceById(theaterId))
                .status(PaymentStatusType.PENDING)
                .build();
        userOrderRepository.save(userOrder);

        List<OrderItem> orderItems = orderList.stream()
                .map(req -> {
                    Inventory inv = inventoryMap.get(req.itemId());
                    if (inv == null || !inv.getIsAvailable()){
                        throw new IllegalArgumentException("존재하지 않거나 판매 중이 아닌 품목입니다.");
                    }
                    if (inv.getQuantity() < req.quantity()){
                        throw new IllegalArgumentException(inv.getItem().getName() + " 재고가 부족합니다.");
                    }

                    inv.setQuantity(inv.getQuantity() - req.quantity());

                    Item item = inv.getItem();
                    return OrderItem.builder()
                            .order(userOrder)
                            .item(item)
                            .quantity(req.quantity())
                            .price(item.getPrice())
                            .build();
                }).toList();

        orderItemRepository.saveAll(orderItems);

        BigDecimal totalPrice = orderItems.stream()
                .map(orderItem ->
                        new BigDecimal(orderItem.getPrice())
                                .multiply(new BigDecimal(orderItem.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        userOrder.setTotalPrice(totalPrice);

        List<ItemListDTO> itemInfo = orderItems.stream()
                .map(ItemListDTO::fromOrderItem)
                .toList();

        return UserOrderResponseDTO.of(userOrder.getUuid(), itemInfo, totalPrice, theaterId);
    }
}
