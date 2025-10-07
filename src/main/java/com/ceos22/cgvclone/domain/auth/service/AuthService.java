package com.ceos22.cgvclone.domain.auth.service;

import com.ceos22.cgvclone.domain.auth.dto.JwtTokenDTO;
import com.ceos22.cgvclone.domain.auth.dto.LoginRequestDTO;
import com.ceos22.cgvclone.domain.auth.dto.LogoutResponseDTO;
import com.ceos22.cgvclone.domain.auth.dto.SignUpRequestDTO;
import com.ceos22.cgvclone.domain.auth.jwt.JwtTokenBlacklist;
import com.ceos22.cgvclone.domain.auth.jwt.TokenProvider;
import com.ceos22.cgvclone.domain.user.entity.User;
import com.ceos22.cgvclone.domain.user.enums.UserRoleType;
import com.ceos22.cgvclone.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenBlacklist jwtTokenBlacklist;


    // 회원가입
    @Transactional
    public void signup(SignUpRequestDTO signUpRequestDTO) {
        if (userRepository.findByEmail(signUpRequestDTO.email()).isPresent()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }

        User user = User.builder()
                .name(signUpRequestDTO.name())
                .email(signUpRequestDTO.email())
                .password(passwordEncoder.encode(signUpRequestDTO.password()))
                .phoneNumber(signUpRequestDTO.phoneNumber())
                .role(UserRoleType.CUSTOMER)
                .build();

        userRepository.save(user);
    }

    // 로그인
    @Transactional
    public JwtTokenDTO login(LoginRequestDTO loginRequestDTO) {
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                loginRequestDTO.email(),
                loginRequestDTO.password()
        );

        Authentication authentication = authenticationManager.authenticate(authToken);

        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();


        String accessToken = tokenProvider.createAccessToken(
                principal.getUsername(),
                authentication
        );

        return new JwtTokenDTO("Bearer", accessToken, null);
    }


    // 로그 아웃
    @Transactional
    public LogoutResponseDTO logout(HttpServletRequest request) {
        String token = tokenProvider.getAccessToken(request);

        if (token == null || token.isEmpty()) {
            return LogoutResponseDTO.builder()
                    .success(false)
                    .details("요청에 토큰이 없습니다.")
                    .build();
        }

        if (!tokenProvider.validateAccessToken(token) || jwtTokenBlacklist.contains(token)) {
            return LogoutResponseDTO.builder()
                    .success(false)
                    .details("만료되었거나 유효하지 않은 토큰입니다.")
                    .build();
        }

        jwtTokenBlacklist.add(token);

        SecurityContextHolder.clearContext();

        return LogoutResponseDTO.builder()
                .success(true)
                .details("로그아웃 성공")
                .build();
    }
}
