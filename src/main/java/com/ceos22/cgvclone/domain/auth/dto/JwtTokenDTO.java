package com.ceos22.cgvclone.domain.auth.dto;

public record JwtTokenDTO(
        String grantType,
        String accessToken,
        String refreshToken
) {
}
