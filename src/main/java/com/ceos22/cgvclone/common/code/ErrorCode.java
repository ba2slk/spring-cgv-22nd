package com.ceos22.cgvclone.common.code;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 잘못된 요청
    BAD_REQUEST_ERROR(400, "G001", "Bad Request Exception"),

    // 인증/인가 관련
    UNAUTHORIZED_ERROR(401, "G002", "Unauthorized Exception"),

    // 허용되지 않은 접근
    FORBIDDEN_ERROR(403, "G003", "Forbidden Exception"),

    // 리소스 부재
    NOT_FOUND_ERROR(404, "G004", "Not Found Exception"),

    // TODO: 임시 -> 도메인별 시나리오를 고려한 설계
    INTERNAL_SERVER_ERROR(500, "G005", "Internal Server Error");

    private final int status;
    private final String divisionCode;
    private final String message;

    ErrorCode(final int status, final String divisionCode, final String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
