package com.ceos22.cgvclone.domain.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JwtTokenBlacklist {

    private final Set<String> invalidTokens = new HashSet<>();

    public void add(String token){
        invalidTokens.add(token);
    }

    public boolean contains(String token) {
        return invalidTokens.contains(token);
    }

}
