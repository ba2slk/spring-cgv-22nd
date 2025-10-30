package com.ceos22.cgvclone.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // pointcut 분리
    @Pointcut("execution(* com.ceos22.cgvclone.domain..controller..*(..))")
    private void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {

        // HTTP Request 정보 획득
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();

        // 메서드 시그니처 & 파라미터 정보
        String controllerName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String params = Arrays.stream(joinPoint.getArgs())
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        log.info("======== HTTP REQUEST [START] ========");
        log.info("==> URI: [{}]{}", method, uri);
        log.info("==> IP: {}", ip);
        log.info("==> Controller: {}.{}", controllerName, methodName);
        log.info("==> Params: {}", params);

        // 실행 시간 측정 시작
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            // 실행 시간을 포함한 예외 로깅
            stopWatch.stop();
            long timeMs = stopWatch.getTotalTimeMillis();
            log.error("======== HTTP EXCEPTION ({}ms) ========", timeMs);
            log.error("==> URI: [{}]{}", method, uri);
            log.error("==> Exception: {}: {}", e.getClass().getSimpleName(), e.getMessage(), e);
            log.info("========================================");
            throw e;
        }

        // 실행 시간 측정 종료
        stopWatch.stop();
        long timeMs = stopWatch.getTotalTimeMillis();
        String resultType = (result != null) ? result.getClass().getSimpleName() : "void";

        log.info("======== HTTP RESPONSE ({}ms) ========", timeMs);
        log.info("==> URI: [{}]{}", method, uri);
        log.info("==> Response Type: {}", resultType);
        log.info("========================================");

        return result;
    }
}
