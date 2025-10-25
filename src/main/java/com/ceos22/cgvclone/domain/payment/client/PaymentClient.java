package com.ceos22.cgvclone.domain.payment.client;

import com.ceos22.cgvclone.config.OpenFeignConfig;
import com.ceos22.cgvclone.domain.payment.dto.InstantPaymentRequestDTO;
import com.ceos22.cgvclone.domain.payment.dto.InstantPaymentResponseDTO;
import com.ceos22.cgvclone.domain.payment.dto.PaymentDetailsResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "portOnePayment",
        url = "https://payment.loopz.co.kr",
        configuration = OpenFeignConfig.class
)
public interface PaymentClient {
    @PostMapping("/payments/{paymentId}/instant")
    InstantPaymentResponseDTO instant(
            @PathVariable("paymentId") String paymentId,
            @RequestBody InstantPaymentRequestDTO requestDto
    );

    @PostMapping("/payments/{paymentId}/cancel")
    PaymentDetailsResponseDTO cancel(@PathVariable("paymentId") String paymentId);

    @GetMapping("/payments/{paymentId}")
    PaymentDetailsResponseDTO get(@PathVariable("paymentId") String paymentId);
}
