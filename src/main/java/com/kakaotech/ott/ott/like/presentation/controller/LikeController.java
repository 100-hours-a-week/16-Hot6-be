package com.kakaotech.ott.ott.like.presentation.controller;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.like.application.service.LikeService;
import com.kakaotech.ott.ott.like.presentation.dto.request.LikeRequestDto;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<ApiResponse> activeLike(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                  @RequestBody @Valid LikeRequestDto likeRequestDto) {

        Long userId = userPrincipal.getId();

        likeService.likePost(userId, likeRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("좋아요 완료", null));
    }

    @DeleteMapping
    public ResponseEntity<Void> deactiveLike(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                  @RequestBody @Valid LikeRequestDto likeRequestDto) {

        Long userId = userPrincipal.getId();

        likeService.unlikePost(userId, likeRequestDto);

        return ResponseEntity.noContent().build();
    }
}
