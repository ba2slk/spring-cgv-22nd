package com.ceos22.cgvclone.domain.movie.repository;

import com.ceos22.cgvclone.domain.movie.entity.Movie;
import com.ceos22.cgvclone.domain.movie.enums.MovieStatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    // 상영 상태에 따른 영화 조회
    Page<Movie> findByStatus(MovieStatusType status, Pageable pageable);
}
