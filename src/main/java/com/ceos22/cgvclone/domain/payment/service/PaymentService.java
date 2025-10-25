package com.ceos22.cgvclone.domain.payment.service;

import com.ceos22.cgvclone.domain.payment.entity.Payable;
import com.ceos22.cgvclone.domain.payment.repository.PaymentRepository;
import com.ceos22.cgvclone.domain.payment.client.PaymentClient;
import com.ceos22.cgvclone.domain.payment.dto.InstantPaymentRequestDTO;
import com.ceos22.cgvclone.domain.payment.dto.InstantPaymentResponseDTO;
import com.ceos22.cgvclone.domain.payment.dto.PaymentDetailsResponseDTO;
import com.ceos22.cgvclone.domain.payment.entity.Payment;
import com.ceos22.cgvclone.domain.payment.enums.PaymentStatusType;
import com.ceos22.cgvclone.domain.reservation.entity.Reservation;
import com.ceos22.cgvclone.domain.reservation.enums.ReservationStatusType;
import com.ceos22.cgvclone.domain.reservation.repository.ReservationRepository;
import com.ceos22.cgvclone.domain.reservation.repository.ReservationSeatRepository;
import com.ceos22.cgvclone.domain.snack.entity.Inventory;
import com.ceos22.cgvclone.domain.snack.entity.OrderItem;
import com.ceos22.cgvclone.domain.snack.entity.UserOrder;
import com.ceos22.cgvclone.domain.snack.repository.InventoryRepository;
import com.ceos22.cgvclone.domain.snack.repository.OrderItemRepository;
import com.ceos22.cgvclone.domain.snack.repository.UserOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentClient paymentClient;

    private final PaymentRepository paymentRepository;

    private final ReservationRepository reservationRepository;
    private final UserOrderRepository userOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryRepository inventoryRepository;

    private final ReservationSeatRepository reservationSeatRepository;

    @Value("${api.portOne.storeId}")
    private String storeId;
    private String currency = "KRW";

    /* UUID 기반 결제 대상을 반환하는 헬퍼 함수 */
    private Payable findPayableByUuid(UUID uuid) {
        Optional<Reservation> reservationOpt = reservationRepository.findByUuid(uuid);
        if (reservationOpt.isPresent()) {
            return reservationOpt.get();
        }

        Optional<UserOrder> userOrderOpt = userOrderRepository.findByUuid(uuid);
        if (userOrderOpt.isPresent()) {
            return userOrderOpt.get();
        }

        throw new EntityNotFoundException("결제 대상을 찾을 수 없습니다. (UUID: " + uuid + ")");
    }

    /* 결제 */
    @Transactional
    public InstantPaymentResponseDTO confirmPayment(
            UUID orderUuid, String email
    ) {
        Payable payableItem = findPayableByUuid(orderUuid);

        if (!payableItem.getUser().getEmail().equals(email)) {
            throw new IllegalStateException("본인의 건만 결제할 수 있습니다.");
        }

        if (payableItem.getStatus() == PaymentStatusType.PAID) {
            Optional<Payment> paymentOpt;
            if (payableItem instanceof Reservation) {
                paymentOpt = paymentRepository.findByReservation((Reservation) payableItem);
            }
            else {
                paymentOpt = paymentRepository.findByUserOrder((UserOrder) payableItem);
            }

            Payment payment = paymentOpt.orElseThrow(
                    () -> new IllegalStateException("결제는 완료되었으나 결제 내역을 찾을 수 없습니다.")
            );

            return new InstantPaymentResponseDTO(
                    payment.getPaymentId(),
                    Date.from(payment.getPaidAt().atZone(ZoneId.systemDefault()).toInstant())
            );
        }

        if (payableItem.getStatus() != PaymentStatusType.PENDING) {
            throw new IllegalStateException("결제 대기 상태가 아닙니다.");
        }

        InstantPaymentRequestDTO paymentRequest = new InstantPaymentRequestDTO(
                storeId,
                payableItem.getOrderName(),
                payableItem.getTotalPrice().intValue(),
                currency,
                null
        );

        InstantPaymentResponseDTO response = paymentClient.instant(
                orderUuid.toString(),
                paymentRequest
        );

        LocalDateTime paidAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(response.paidAt().getTime()),
                ZoneId.systemDefault()
        );

        Payment.PaymentBuilder payment = Payment.builder()
                .paymentId(response.paymentId())
                .amount(payableItem.getTotalPrice())
                .paidAt(paidAt)
                .paymentStatus(PaymentStatusType.PAID)
                .pgProvider("portOne")
                .orderName(paymentRequest.orderName())
                .currency(currency);

        if (payableItem instanceof Reservation) {
            payment.reservation((Reservation) payableItem);
        } else if (payableItem instanceof UserOrder) {
            payment.userOrder((UserOrder) payableItem);
        }

        paymentRepository.save(payment.build());

        payableItem.confirm();

        return response;
    }

    /* 결제 취소 */
    @Transactional
    public PaymentDetailsResponseDTO cancelPayment(String paymentId, String email) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("결제 내역을 찾을 수 없습니다."));

        Payable payableItem = payment.getPayable();
        if (payableItem == null) {
            throw new IllegalStateException("결제 내역에 연결된 주문이나 예약이 없습니다.");
        }

        if (!payableItem.getUser().getEmail().equals(email)) {
            throw new IllegalStateException("본인의 예약만 취소할 수 있습니다.");
        }

        if (payment.getPaymentStatus() == PaymentStatusType.CANCELED) {
            throw new IllegalStateException("이미 취소된 결제입니다.");
        }

        if (payableItem instanceof Reservation reservation) {
            if (reservation.getShowtime().getStartTime().isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("이미 상영 시작 시간이 지난 예매 건입니다.");
            }
        }

        PaymentDetailsResponseDTO response = paymentClient.cancel(paymentId);

        payment.cancel();

        payableItem.cancel();

        if (payableItem instanceof Reservation reservation) {
            reservationSeatRepository.deleteAllByReservation(reservation);
        }
        else if (payableItem instanceof UserOrder userOrder) {
            restoreStock(userOrder);
        }

        return response;
    }

    /* 결제 단건 조회 */
    @Transactional(readOnly = true)
    public PaymentDetailsResponseDTO getPaymentDetails(String paymentId, String email) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("결제 내역을 찾을 수 없습니다."));

        Payable payableItem = payment.getPayable();
        if (payableItem == null || !payableItem.getUser().getEmail().equals(email)) {
            throw new IllegalStateException("본인의 결제 내역만 조회할 수 있습니다.");
        }

        return paymentClient.get(paymentId);
    }

    /* 주문 취소 시 재고 복구 */
    private void restoreStock(UserOrder userOrder) {
        List<OrderItem> orderItems = orderItemRepository.findByOrder(userOrder);

        List<Long> itemIds = orderItems.stream()
                .map(orderItem -> orderItem.getItem().getId())
                .toList();

        Long theaterId = userOrder.getTheater().getId();

        List<Inventory> lockedInventories = inventoryRepository.findByTheaterIdAndItemIdInWithLock(theaterId, itemIds);

        Map<Long, Inventory> inventoryMap = lockedInventories.stream()
                .collect(Collectors.toMap(inv -> inv.getItem().getId(), inv -> inv));

        for (OrderItem orderItem : orderItems) {
            Inventory inventory = inventoryMap.get(orderItem.getItem().getId());
            if (inventory != null) {
                inventory.setQuantity(inventory.getQuantity() + orderItem.getQuantity());
            }
        }
    }
}
