package com.ceos22.cgvclone.domain.payment.dto;

import com.ceos22.cgvclone.domain.payment.enums.PaymentStatusType;

import java.util.Date;

public record PaymentDetailsResponseDTO(
        String paymentId,
        PaymentStatusType paymentStatus,
        String orderName,
        String pgProvider,
        String currency,
        String customData,
        Date paidAt
) {
}