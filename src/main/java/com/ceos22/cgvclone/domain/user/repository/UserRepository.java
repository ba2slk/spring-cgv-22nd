package com.ceos22.cgvclone.domain.user.repository;

import com.ceos22.cgvclone.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
