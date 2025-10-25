package com.ceos22.cgvclone.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
@EnableFeignClients("com.ceos22.cgvclone.domain.payment")
public class OpenFeignConfig {
    @Value("${api.portOne.token}")
    private String portOneToken;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("Authorization", "Bearer " + portOneToken);
        };
    }
}
