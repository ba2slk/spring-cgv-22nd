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

    /* 결제 */
    @Transactional
    public InstantPaymentResponseDTO confirmPayment(UUID orderUuid, String email) {

        // Payable 대상 유효성 검사
        Payable payableItem = findAndValidatePayableItem(orderUuid, email);

        // 멱등성 처리
        Optional<InstantPaymentResponseDTO> idempotentResponse = checkConfirmationIdempotency(payableItem);
        if (idempotentResponse.isPresent()) {
            return idempotentResponse.get();
        }

        // 결제 대기 상태 여부 확인
        validatePaymentStatus(payableItem, PaymentStatusType.PENDING);

        // 결제 요청을 위한 dto 생성
        InstantPaymentRequestDTO paymentRequest = createPaymentRequest(payableItem);

        // 결제 요청
        InstantPaymentResponseDTO response = paymentClient.instant(
                orderUuid.toString(),
                paymentRequest
        );

        // 결제 정보를 Payment 테이블에 저장
        Payment payment = buildPaymentEntity(payableItem, response, paymentRequest);
        paymentRepository.save(payment);

        // 결제 대상 객체의 테이블에서도 결제 상태를 갱신 PENDING -> PAID
        payableItem.confirm();

        return response;
    }

    /* 결제 취소 */
    @Transactional
    public PaymentDetailsResponseDTO cancelPayment(String paymentId, String email) {

        // Payable 대상 유효성 검사
        Payment payment = findAndValidatePayment(paymentId, email);

        // 결제 대상 획득
        Payable payableItem = payment.getPayable();

        // 취소 정책 위반 여부 확인
        validateCancellationPolicy(payment, payableItem);

        // 결제 취소 요청
        PaymentDetailsResponseDTO response = paymentClient.cancel(paymentId);

        // 결제 상태 PAID -> CANCELED 변경
        payment.cancel();

        payableItem.cancel();

        // Payable 구현체에 따라 각기 다른 테이블 갱신 방법이 필요함.
        handleDomainSpecificCancellation(payableItem);

        return response;
    }

    /* 결제 단건 조회 */
    @Transactional(readOnly = true)
    public PaymentDetailsResponseDTO getPaymentDetails(String paymentId, String email) {

        // paymentId와 결제 요청자의 유효성 검사
        findAndValidatePayment(paymentId, email);

        // 결제 단건 조회 결과를 그대로 반환
        return paymentClient.get(paymentId);
    }

    /***** 헬퍼 함수 *****/
    /* UUID 기반 결제 대상을 반환하는 헬퍼 함수  */
    private Payable findAndValidatePayableItem(UUID uuid, String email) {
        Payable payableItem;

        Optional<Reservation> reservationOpt = reservationRepository.findByUuid(uuid);
        if (reservationOpt.isPresent()) {
            payableItem = reservationOpt.get();
        } else {
            payableItem = userOrderRepository.findByUuid(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("결제 대상을 찾을 수 없습니다. (UUID: " + uuid + ")"));
        }

        if (!payableItem.getUser().getEmail().equals(email)) {
            throw new IllegalStateException("본인의 건만 결제할 수 있습니다.");
        }
        return payableItem;
    }

    /* Payment ID 기반 결제 내역 반환  */
    private Payment findAndValidatePayment(String paymentId, String email) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("결제 내역을 찾을 수 없습니다."));

        Payable payableItem = payment.getPayable();
        if (payableItem == null) {
            throw new IllegalStateException("결제 내역에 연결된 주문이나 예약이 없습니다.");
        }
        if (!payableItem.getUser().getEmail().equals(email)) {
            throw new IllegalStateException("본인의 결제 내역만 조회할 수 있습니다.");
        }
        return payment;
    }

    /* 결제 승인 멱등성 확인 */
    private Optional<InstantPaymentResponseDTO> checkConfirmationIdempotency(Payable payableItem) {
        if (payableItem.getStatus() == PaymentStatusType.PAID) {
            Optional<Payment> paymentOpt;
            if (payableItem instanceof Reservation) {
                paymentOpt = paymentRepository.findByReservation((Reservation) payableItem);
            } else {
                paymentOpt = paymentRepository.findByUserOrder((UserOrder) payableItem);
            }

            Payment payment = paymentOpt.orElseThrow(
                    () -> new IllegalStateException("결제는 완료되었으나 결제 내역을 찾을 수 없습니다.")
            );

            return Optional.of(new InstantPaymentResponseDTO(
                    payment.getPaymentId(),
                    Date.from(payment.getPaidAt().atZone(ZoneId.systemDefault()).toInstant())
            ));
        }
        return Optional.empty();
    }

    /* 결제 대상의 상태 검증 */
    private void validatePaymentStatus(Payable payableItem, PaymentStatusType expectedStatus) {
        if (payableItem.getStatus() != expectedStatus) {
            throw new IllegalStateException("결제 상태가 올바르지 않습니다. (기대: " + expectedStatus + ", 실제: " + payableItem.getStatus() + ")");
        }
    }

    /* Mock API 요청 DTO 생성 */
    private InstantPaymentRequestDTO createPaymentRequest(Payable payableItem) {
        return new InstantPaymentRequestDTO(
                storeId,
                payableItem.getOrderName(),
                payableItem.getTotalPrice().intValue(),
                currency,
                null
        );
    }

    /* Payment 엔티티 빌드 */
    private Payment buildPaymentEntity(Payable payableItem, InstantPaymentResponseDTO response, InstantPaymentRequestDTO request) {
        LocalDateTime paidAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(response.paidAt().getTime()),
                ZoneId.systemDefault()
        );

        Payment.PaymentBuilder paymentBuilder = Payment.builder()
                .paymentId(response.paymentId())
                .amount(payableItem.getTotalPrice())
                .paidAt(paidAt)
                .paymentStatus(PaymentStatusType.PAID)
                .pgProvider("portOne")
                .orderName(request.orderName())
                .currency(currency);

        if (payableItem instanceof Reservation) {
            paymentBuilder.reservation((Reservation) payableItem);
        } else if (payableItem instanceof UserOrder) {
            paymentBuilder.userOrder((UserOrder) payableItem);
        }

        return paymentBuilder.build();
    }

    /* 결제 취소 정책 검증 */
    private void validateCancellationPolicy(Payment payment, Payable payableItem) {
        if (payment.getPaymentStatus() == PaymentStatusType.CANCELED) {
            throw new IllegalStateException("이미 취소된 결제입니다.");
        }

        if (payableItem instanceof Reservation reservation) {
            if (reservation.getShowtime().getStartTime().isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("이미 상영 시작 시간이 지난 예매 건입니다.");
            }
        }
    }

    /* 도메인별 취소 후처리 */
    private void handleDomainSpecificCancellation(Payable payableItem) {
        if (payableItem instanceof Reservation reservation) {
            reservationSeatRepository.deleteAllByReservation(reservation);
        } else if (payableItem instanceof UserOrder userOrder) {
            restoreStock(userOrder);
        }
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
