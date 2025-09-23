package com.ceos22.cgvclone.domain.theater.repository;

import com.ceos22.cgvclone.domain.theater.entity.Theater;
import com.ceos22.cgvclone.domain.theater.entity.TheaterLike;
import com.ceos22.cgvclone.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterLikeRepository extends JpaRepository<TheaterLike, Long> {
    boolean existsByUserAndTheater(User user, Theater theater);
    void deleteByUserAndTheater(User user, Theater theater);
}
