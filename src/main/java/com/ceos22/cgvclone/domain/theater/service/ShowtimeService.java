package com.ceos22.cgvclone.domain.theater.service;

import com.ceos22.cgvclone.domain.movie.service.MovieService;
import com.ceos22.cgvclone.domain.theater.DTO.ShowtimeListResponseDTO;
import com.ceos22.cgvclone.domain.theater.entity.Showtime;
import com.ceos22.cgvclone.domain.theater.repository.ShowtimeRepository;
import com.ceos22.cgvclone.domain.theater.repository.TheaterMovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowtimeService {
    private final ShowtimeRepository showtimeRepository;
    private final TheaterMovieRepository theaterMovieRepository;


    /* 상영 시간 목록 조회 */
    @Transactional(readOnly = true)
    public List<ShowtimeListResponseDTO> getShowtimeList(Long theaterId, Long movieId) {

        List<Showtime> showtime = showtimeRepository.findShowtimeWithDetails(movieId, theaterId);

        return showtime.stream()
                .map(s -> new ShowtimeListResponseDTO(
                        s.getId(),
                        s.getMovie().getTitle(),
                        s.getScreen().getType(),
                        s.getScreen().getName(),
                        s.getScreen().getPrice(),
                        s.getStartTime(),
                        s.getEndTime()
                ))
                .toList();
    }

}
