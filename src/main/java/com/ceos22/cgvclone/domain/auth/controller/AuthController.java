package com.ceos22.cgvclone.domain.auth.controller;

import com.ceos22.cgvclone.domain.auth.dto.JwtTokenDTO;
import com.ceos22.cgvclone.domain.auth.dto.LoginRequestDTO;
import com.ceos22.cgvclone.domain.auth.dto.LogoutResponseDTO;
import com.ceos22.cgvclone.domain.auth.dto.SignUpRequestDTO;
import com.ceos22.cgvclone.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        authService.signup(signUpRequestDTO);
        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<JwtTokenDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        JwtTokenDTO jwtTokenDTO = authService.login(loginRequestDTO);
        return ResponseEntity.ok(jwtTokenDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDTO> logout(HttpServletRequest request) {
        LogoutResponseDTO result = authService.logout(request);
        HttpStatus status = result.success() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(result);
    }
}
