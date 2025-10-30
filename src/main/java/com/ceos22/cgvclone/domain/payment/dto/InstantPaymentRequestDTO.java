package com.ceos22.cgvclone.domain.payment.dto;

public record InstantPaymentRequestDTO(
        String storeId,             // CEOS-22nd-3344556
        String orderName,           // 주문 요약
        Integer totalPayAmount,     // 최종 결제 금액
        String currency,            // 화폐 단위
        String customData           // Optional
) {
}

/*
InstantPaymentRequest

storeId : string
orderName : string
totalPayAmount : integer
currency : string (KRW, USD)
customData : string (optional)
 */
