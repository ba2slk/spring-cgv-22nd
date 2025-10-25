package com.ceos22.cgvclone.domain.payment.entity;

import com.ceos22.cgvclone.domain.payment.enums.PaymentStatusType;
import com.ceos22.cgvclone.domain.user.entity.User;

import java.math.BigDecimal;
import java.util.UUID;

public interface Payable {
    UUID getUuid();

    User getUser();

    PaymentStatusType getStatus();

    BigDecimal getTotalPrice();

    String getOrderName();

    void confirm();

    void cancel();
}
