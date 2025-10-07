package com.ceos22.cgvclone.domain.theater.entity;

import com.ceos22.cgvclone.domain.theater.enums.ScreenType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Screen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id")
    private Theater theater;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ScreenType type = ScreenType.NORMAL;

    private String name;
}
