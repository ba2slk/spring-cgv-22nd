package com.ceos22.cgvclone.config.exception;

import com.ceos22.cgvclone.common.code.ErrorCode;
import com.ceos22.cgvclone.common.response.ErrorResponse;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.action.internal.EntityActionVetoException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 공통 응답 생성
    private ResponseEntity<ErrorResponse> buildResponse(ErrorCode errorCode, String reason) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode, reason));
    }

    // 유효성 검사 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return buildResponse(ErrorCode.BAD_REQUEST_ERROR, errorMessage);
    }


    // 인증/인가 관련
    @ExceptionHandler(SecurityException.class)
    protected ResponseEntity<ErrorResponse> handleSecurityException(SecurityException e) {
        return buildResponse(ErrorCode.UNAUTHORIZED_ERROR, e.getMessage());
    }


    // 잘못된 경로 접근
    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException e) {
        return buildResponse(ErrorCode.NOT_FOUND_ERROR, e.getMessage());
    }

    // 허용되지 않은 접근
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        return buildResponse(ErrorCode.FORBIDDEN_ERROR, e.getMessage());
    }

    // 존재하지 않는 엔티티 접근
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        return buildResponse(ErrorCode.NOT_FOUND_ERROR, e.getMessage());
    }

    // 서비스 로직 위반
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        return buildResponse(ErrorCode.BAD_REQUEST_ERROR, e.getMessage());
    }

    // 외부 API 호출 중 발생한 FeignException 처리
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException e) {
        return ResponseEntity.status(e.status())
                .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage()));
    }

    // 그 밖의 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception e) {
        log.error("Unhandled exception occurred: ", e);
        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
