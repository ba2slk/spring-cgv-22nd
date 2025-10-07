package com.ceos22.cgvclone.domain.auth.dto;

import lombok.Builder;

@Builder
public record LogoutResponseDTO(
        boolean success,
        String details
) {
}
