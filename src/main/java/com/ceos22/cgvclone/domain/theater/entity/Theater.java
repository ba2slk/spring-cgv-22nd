package com.ceos22.cgvclone.domain.theater.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Theater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @OneToMany(mappedBy = "theater", fetch = FetchType.LAZY)
    private List<Screen> screens = new ArrayList<>();

    private String details;

    private String parkingInfo;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isOpen = Boolean.FALSE;
}
