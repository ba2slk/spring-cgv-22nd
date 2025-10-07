package com.ceos22.cgvclone.domain.theater.service;

import com.ceos22.cgvclone.domain.theater.entity.Theater;
import com.ceos22.cgvclone.domain.theater.entity.TheaterLike;
import com.ceos22.cgvclone.domain.theater.repository.TheaterLikeRepository;
import com.ceos22.cgvclone.domain.theater.repository.TheaterRepository;
import com.ceos22.cgvclone.domain.user.entity.User;
import com.ceos22.cgvclone.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TheaterLikeService {

    private final UserRepository userRepository;
    private final TheaterRepository theaterRepository;
    private final TheaterLikeRepository theaterLikeRepository;

    /* 영화관 찜하기*/
    @Transactional
    public void likeTheater(Long userId, Long theaterId) {
        User user = userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("no user")); // TODO: 예외 처리 전략
        Theater theater = theaterRepository.findById(theaterId).orElseThrow(()->new IllegalArgumentException("no theater"));

        if (theaterLikeRepository.existsByUserAndTheater(user, theater)){
            throw new IllegalArgumentException("이미 찜한 영화관입니다.");
        }

        TheaterLike theaterLike = TheaterLike.builder()
                .user(user)
                .theater(theater)
                .build();

        theaterLikeRepository.save(theaterLike);
    }

    /* 영화관 찜 취소하기 */
    @Transactional
    public void unlikeTheater(Long userId, Long theaterId) {
        User user = userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("no user"));
        Theater theater = theaterRepository.findById(theaterId).orElseThrow(()->new IllegalArgumentException("no theater"));

        theaterLikeRepository.deleteByUserAndTheater(user, theater);
    }
}
