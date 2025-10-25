package com.ceos22.cgvclone.domain.theater.repository;

import com.ceos22.cgvclone.domain.theater.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    @Query("SELECT s FROM Showtime s " +
            "JOIN FETCH s.movie m " +
            "JOIN FETCH s.screen sc " +
            "WHERE m.id = :movieId AND sc.theater.id = :theaterId")
    List<Showtime> findShowtimeWithDetails(@Param("movieId") Long movieId, @Param("theaterId") Long theaterId);

    @Query("SELECT s FROM Showtime s JOIN FETCH s.screen sc WHERE s.id = :showtimeId")
    Optional<Showtime> findWithScreenById(@Param("showtimeId") Long showtimeId);
}
