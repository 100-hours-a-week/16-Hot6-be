package com.kakaotech.ott.ott.like.presentation.controller;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.like.application.service.LikeService;
import com.kakaotech.ott.ott.like.presentation.dto.request.LikeActiveRequestDto;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<ApiResponse> activeLike(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                  @RequestBody @Valid LikeActiveRequestDto likeActiveRequestDto) {

        Long userId = userPrincipal.getId();

        likeService.likePost(userId, likeActiveRequestDto);

        return ResponseEntity.ok(ApiResponse.success("좋아요 완료", null));
    }
}
