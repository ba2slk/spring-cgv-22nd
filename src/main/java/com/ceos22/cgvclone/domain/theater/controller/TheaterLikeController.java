package com.ceos22.cgvclone.domain.theater.controller;

import com.ceos22.cgvclone.domain.theater.service.TheaterLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TheaterLikeController {

    private final TheaterLikeService theaterLikeService;

    /* 영화관 찜 하기 */
    @PostMapping("/api/theater/{theaterId}/like")
    public ResponseEntity<Void> likeTheater(@PathVariable Long theaterId,
                                            @RequestParam Long userId){ // TODO: Spring Security 기반 User 정보 획득
        theaterLikeService.likeTheater(userId, theaterId);
        return ResponseEntity.ok().build();
    }

    /* 영화관 찜 취소하기 */
    @DeleteMapping("/api/theater/{theaterId}/like")
    public ResponseEntity<Void> unlikeTheater(@PathVariable Long theaterId,
                                              @RequestParam Long userId){ // TODO: Spring Security 기반 User 정보 획득
        theaterLikeService.unlikeTheater(userId, theaterId);
        return ResponseEntity.ok().build();
    }
}
