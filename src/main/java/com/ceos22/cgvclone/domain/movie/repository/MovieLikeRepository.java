package com.ceos22.cgvclone.domain.movie.repository;

import com.ceos22.cgvclone.domain.movie.entity.MovieLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieLikeRepository extends JpaRepository<MovieLike, Long> {
}
