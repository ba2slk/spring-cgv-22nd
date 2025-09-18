package com.ceos22.cgvclone.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @UuidGenerator
    @Column(name = "uuid", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID uuid;

    @Column(unique = true, nullable = false)
    private String email;

    // 서비스 계층에서 반드시 Hash 처리
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String phoneNumber;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public User(String email, String password, String name, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
