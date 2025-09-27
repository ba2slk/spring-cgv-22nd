package com.ceos22.cgvclone.domain.auth.dto;

public record LoginRequestDTO(
        String email,
        String password
) {}
