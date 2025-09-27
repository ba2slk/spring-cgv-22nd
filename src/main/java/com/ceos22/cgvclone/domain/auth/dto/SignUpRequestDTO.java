package com.ceos22.cgvclone.domain.auth.dto;

import com.ceos22.cgvclone.domain.user.enums.UserRoleType;

public record SignUpRequestDTO(
        String name,
        String email,
        String password,
        String phoneNumber,
        UserRoleType role
) {}
