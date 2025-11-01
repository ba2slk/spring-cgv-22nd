package com.ceos22.cgvclone.domain.user.repository;

import com.ceos22.cgvclone.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUuid(UUID uuid);
    Optional<User> findByEmail(String email);
    User getReferenceByUuid(UUID uuid);
}
