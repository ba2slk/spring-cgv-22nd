package com.ceos22.cgvclone.domain.movie.service;

import com.ceos22.cgvclone.domain.movie.entity.Movie;
import com.ceos22.cgvclone.domain.movie.entity.MovieLike;
import com.ceos22.cgvclone.domain.movie.repository.MovieLikeRepository;
import com.ceos22.cgvclone.domain.movie.repository.MovieRepository;
import com.ceos22.cgvclone.domain.user.entity.User;
import com.ceos22.cgvclone.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MovieLikeService {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final MovieLikeRepository movieLikeRepository;


    /* 영화 찜하기 */
    @Transactional
    public void likeMovie(Long userId, Long movieId) {
        User user = userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("no user")); // TODO: 예외 처리 전략
        Movie movie = movieRepository.findById(movieId).orElseThrow(()-> new IllegalArgumentException("no movie"));

        if (movieLikeRepository.existsByUserAndMovie(user, movie)) {
            throw new IllegalArgumentException("이미 찜한 영화입니다."); // TODO: 현재 상황에서는 500 에러 발생
        }

        MovieLike movieLike = MovieLike.builder()
                .user(user)
                .movie(movie)
                .build();

        movieLikeRepository.save(movieLike);
    }

    /* 영화 찜 취소하기 */
    @Transactional
    public void unlikeMovie(Long userId, Long movieId) {
        User user = userRepository.findById(userId).orElseThrow();
        Movie movie = movieRepository.findById(movieId).orElseThrow();

        movieLikeRepository.deleteByUserAndMovie(user, movie);
    }
}
