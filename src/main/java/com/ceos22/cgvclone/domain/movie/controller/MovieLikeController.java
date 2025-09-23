package com.ceos22.cgvclone.domain.movie.controller;

import com.ceos22.cgvclone.domain.movie.service.MovieLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MovieLikeController {

    private final MovieLikeService movieLikeService;

    /* 영화 찜하기 */
    @PostMapping("/api/movie/{movieId}/like")
    public ResponseEntity<Void> likeMovie(@PathVariable Long movieId,
                                          @RequestParam Long userId){  // TODO: Spring Security 기반 User 정보 획득
        movieLikeService.likeMovie(userId, movieId);
        return ResponseEntity.ok().build();
    }

    /* 영화 찜 취소하기 */
    @DeleteMapping("/api/movie/{movieId}/like")
    public ResponseEntity<Void> unlikeMovie(@PathVariable Long movieId,
                                            @RequestParam Long userId){ // TODO: Spring Security 기반 User 정보 획득
        movieLikeService.unlikeMovie(userId, movieId);
        return ResponseEntity.ok().build();
    }
}
