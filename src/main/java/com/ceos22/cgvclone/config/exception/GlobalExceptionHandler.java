package com.ceos22.cgvclone.config.exception;

import com.ceos22.cgvclone.common.code.ErrorCode;
import com.ceos22.cgvclone.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 유효성 검사 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        ErrorResponse response = ErrorResponse.of(ErrorCode.BAD_REQUEST_ERROR, errorMessage);
        return ResponseEntity.status(ErrorCode.BAD_REQUEST_ERROR.getStatus()).body(response);
    }


    // 인증/인가 관련
    @ExceptionHandler(SecurityException.class)
    protected ResponseEntity<ErrorResponse> handleSecurityException(SecurityException ex) {
        ErrorResponse response = ErrorResponse.of(ErrorCode.UNAUTHORIZED_ERROR, ex.getMessage());
        return ResponseEntity.status(ErrorCode.UNAUTHORIZED_ERROR.getStatus()).body(response);
    }


    // 잘못된 경로 접근
    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_FOUND_ERROR, ex.getMessage());
        return ResponseEntity.status(ErrorCode.NOT_FOUND_ERROR.getStatus()).body(response);
    }

    // 허용되지 않은 접근
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse response = ErrorResponse.of(ErrorCode.FORBIDDEN_ERROR, ex.getMessage());
        return ResponseEntity.status(ErrorCode.FORBIDDEN_ERROR.getStatus()).body(response);
    }

    // 그 밖의 모든 에러 TODO: (임시) -> 도메인 별 세분화 필요
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus()).body(response);
    }
}
