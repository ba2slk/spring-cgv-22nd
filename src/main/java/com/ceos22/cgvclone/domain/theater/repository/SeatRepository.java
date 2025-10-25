package com.ceos22.cgvclone.domain.theater.repository;

import com.ceos22.cgvclone.domain.theater.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByScreen_Id(Long screenId);
}
