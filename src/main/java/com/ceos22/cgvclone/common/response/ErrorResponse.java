package com.ceos22.cgvclone.common.response;

import com.ceos22.cgvclone.common.code.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private int status;
    private String divisionCode;
    private String resultMessage;
    private String reason;

    public static ErrorResponse of(ErrorCode errorCode, String reason) {
        ErrorResponse response = new ErrorResponse();
        response.status = errorCode.getStatus();
        response.divisionCode = errorCode.getDivisionCode();
        response.resultMessage = errorCode.getMessage();
        response.reason = reason;
        return response;
    }
}
