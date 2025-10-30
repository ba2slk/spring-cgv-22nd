package com.ceos22.cgvclone.domain.theater.service;

import com.ceos22.cgvclone.domain.reservation.repository.ReservationSeatRepository;
import com.ceos22.cgvclone.domain.theater.DTO.SeatInfo;
import com.ceos22.cgvclone.domain.theater.DTO.SeatListResponseDTO;
import com.ceos22.cgvclone.domain.theater.entity.Screen;
import com.ceos22.cgvclone.domain.theater.entity.Seat;
import com.ceos22.cgvclone.domain.theater.entity.Showtime;
import com.ceos22.cgvclone.domain.theater.repository.SeatRepository;
import com.ceos22.cgvclone.domain.theater.repository.ShowtimeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatService {
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    public SeatListResponseDTO getSeatList(Long showtimeId){
        Showtime showtime = showtimeRepository.findWithScreenById(showtimeId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상영 시간을 찾을 수 없습니다."));

        Screen screen = showtime.getScreen();

        List<Seat> seatList = seatRepository.findByScreen_Id(screen.getId());

        List<Long> reservedSeatIdList = reservationSeatRepository.findReservedSeatIdsByShowtimeId(showtimeId);

        Set<Long> reservedSeatIds = new HashSet<>(reservedSeatIdList);

        List<SeatInfo> seatInfoList = seatList.stream()
                .map(seat -> new SeatInfo(
                        seat.getId(),
                        seat.getRowNo(),
                        seat.getColNo(),
                        reservedSeatIds.contains(seat.getId()) // O(1) 시간 복잡도로 예약 여부 확인
                ))
                .toList();

        return new SeatListResponseDTO(
                screen.getId(),
                screen.getName(),
                screen.getType(),
                seatInfoList
        );
    }

}
