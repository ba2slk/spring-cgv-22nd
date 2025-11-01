package com.ceos22.cgvclone.domain.auth.service;

import com.ceos22.cgvclone.domain.auth.CustomUserDetails;
import com.ceos22.cgvclone.domain.user.entity.User;
import com.ceos22.cgvclone.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /* 로그인 사용자 이메일을 기반으로 CustomUserDetails 객체를 반환 */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        /* User 객체 획득 */
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return new CustomUserDetails(user);
    }
}
