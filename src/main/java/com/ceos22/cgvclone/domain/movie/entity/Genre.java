package com.ceos22.cgvclone.domain.movie.entity;

import com.ceos22.cgvclone.domain.movie.enums.GenreType;
import com.ceos22.cgvclone.domain.movie.enums.MovieStatusType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private GenreType type = GenreType.UNKNOWN;
}
