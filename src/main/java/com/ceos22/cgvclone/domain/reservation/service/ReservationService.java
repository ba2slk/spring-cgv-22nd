package com.ceos22.cgvclone.domain.reservation.service;

import com.ceos22.cgvclone.domain.reservation.dto.ReservationCancelDTO;
import com.ceos22.cgvclone.domain.reservation.dto.ReservationRequestDTO;
import com.ceos22.cgvclone.domain.reservation.dto.ReservationResponseDTO;
import com.ceos22.cgvclone.domain.reservation.entity.Reservation;
import com.ceos22.cgvclone.domain.reservation.entity.ReservationSeat;
import com.ceos22.cgvclone.domain.reservation.repository.ReservationRepository;
import com.ceos22.cgvclone.domain.reservation.repository.ReservationSeatRepository;
import com.ceos22.cgvclone.domain.theater.entity.Seat;
import com.ceos22.cgvclone.domain.theater.entity.Showtime;
import com.ceos22.cgvclone.domain.theater.repository.SeatRepository;
import com.ceos22.cgvclone.domain.theater.repository.ShowtimeRepository;
import com.ceos22.cgvclone.domain.user.entity.User;
import com.ceos22.cgvclone.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final UserRepository userRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    /* 예매 */
    @Transactional
    public ReservationResponseDTO createReservation(ReservationRequestDTO reservationRequestDTO, Long userId) {  // TODO: Spring Security 기반 User 정보 획득
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Showtime showtime = showtimeRepository.findById(reservationRequestDTO.showtimeId())
                .orElseThrow(() -> new IllegalArgumentException("Showtime not found: " + reservationRequestDTO.showtimeId()));

        List<Long> requestedSeatIds = reservationRequestDTO.seatIds();  // 요청한 좌석 Id

        List<Seat> seats = seatRepository.findAllById(requestedSeatIds);  // 유효 좌석 리스트

        if (seats.size() != requestedSeatIds.size()) {
            throw new IllegalArgumentException("존재하지 않는 좌석이 포함되어 있습니다.");
        }

        // 중복 예매 방지: 특정 상영 시간대와 좌석 목록에 대해 이미 예약된 좌석이 있는지 확인
        if (reservationSeatRepository.existsByReservationShowtimeAndSeatIn(showtime, seats)){
            throw new IllegalStateException("이미 예약된 좌석이 포함되어 있습니다.");
        }

        // Reservation -> 예매 정보 추가
        Reservation reservation = Reservation.builder()
                .user(user)
                .showtime(showtime)
                .build();
        reservationRepository.save(reservation);

        // ReservationSeat -> (예약, 좌석) 추가
        List<ReservationSeat> reservationSeats = seats.stream()
                .map(seat -> ReservationSeat.create(reservation, seat))
                .toList();
        reservationSeatRepository.saveAll(reservationSeats);

        List<Long> reservedSeatIds = seats.stream()
                .map(Seat::getId)
                .toList();

        return new ReservationResponseDTO(
                reservation.getUuid(),
                showtime.getId(),
                reservedSeatIds,
                reservation.getStatus().name()
        );
    }

    /* 예매 취소 */
    @Transactional
    public ReservationResponseDTO cancelReservation (ReservationCancelDTO reservationCancelDTO, Long userId) {  // TODO: Spring Security 기반 User 정보 획득
        Reservation reservation = reservationRepository.findByUuid(reservationCancelDTO.uuid())
                .orElseThrow(()->new IllegalArgumentException("Reservation not found: " + reservationCancelDTO.uuid()));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new IllegalStateException("이미 취소된 예매 건입니다.");
        }

        if (reservation.getShowtime().getStartTime().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalStateException("이미 상영 시작 시간이 지난 예매 건입니다.");
        }

        reservationSeatRepository.deleteAllByReservation(reservation);

        reservation.cancel();

        return new ReservationResponseDTO(
                reservation.getUuid(),
                reservation.getShowtime().getId(),
                List.of(),
                reservation.getStatus().name()
        );
    }
}
