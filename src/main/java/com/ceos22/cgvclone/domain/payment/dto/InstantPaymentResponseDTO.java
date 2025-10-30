package com.ceos22.cgvclone.domain.payment.dto;

import java.util.Date;

public record InstantPaymentResponseDTO(
        String paymentId,
        Date paidAt
) {
}
