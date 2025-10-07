package com.ceos22.cgvclone;

import com.ceos22.cgvclone.domain.movie.dto.MovieDetailsDTO;
import com.ceos22.cgvclone.domain.movie.dto.MovieListDTO;
import com.ceos22.cgvclone.domain.movie.entity.Movie;
import com.ceos22.cgvclone.domain.movie.enums.MovieStatusType;
import com.ceos22.cgvclone.domain.movie.enums.MovieRatingType;
import com.ceos22.cgvclone.domain.movie.repository.MovieRepository;
import com.ceos22.cgvclone.domain.movie.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Movie createTestMovie() {
        return Movie.builder()
                .id(1L)
                .title("테스트 더 무비")
                .image("http://image.source")
                .star(3.4)
                .status(MovieStatusType.ONSCREEN)
                .rating(MovieRatingType.TWELVE)
                .director("세오스")
                .duration(120)
                .releaseDate(LocalDate.of(2025, 9, 27))
                .build();
    }

    @Test
    @DisplayName("상영 중인 영화 목록 조회 성공")
    void getMoviesTest() {
        // given
        Movie movie = createTestMovie();

        Page<Movie> moviePage = new PageImpl<>(List.of(movie));
        when(movieRepository.findByStatus(MovieStatusType.ONSCREEN, Pageable.unpaged()))
                .thenReturn(moviePage);

        // when
        List<MovieListDTO> result = movieService.getMovies(MovieStatusType.ONSCREEN, Pageable.unpaged());

        // then
        assertThat(result).hasSize(1);
        MovieListDTO dto = result.get(0);
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.title()).isEqualTo("테스트 더 무비");
        assertThat(dto.star()).isEqualTo(3.4);
    }

    @Test
    @DisplayName("영화 id를 기반으로 영화 상세 정보를 조회 성공")
    void getMovieTest() {
        // given
        Movie movie = createTestMovie();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        // when
        MovieDetailsDTO result = movieService.getMovie(1L);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("테스트 더 무비");
        assertThat(result.star()).isEqualTo(3.4);
        assertThat(result.rating()).isEqualTo(MovieRatingType.TWELVE);
        assertThat(result.director()).isEqualTo("세오스");
        assertThat(result.duration()).isEqualTo(120);
        assertThat(result.releaseDate()).isEqualTo(LocalDate.of(2025, 9, 27));
    }
}
