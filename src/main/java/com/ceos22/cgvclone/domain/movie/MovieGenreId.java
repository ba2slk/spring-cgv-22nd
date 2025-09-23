package com.ceos22.cgvclone.domain.movie;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
public class MovieGenreId implements Serializable {
    private Long movie;
    private Long genre;
}
