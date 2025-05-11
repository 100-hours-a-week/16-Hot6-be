package com.kakaotech.ott.ott.scrap.presentation.controller;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.like.presentation.dto.request.LikeRequestDto;
import com.kakaotech.ott.ott.scrap.application.service.ScrapService;
import com.kakaotech.ott.ott.scrap.presentation.dto.request.ScrapRequestDto;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/scraps")
@RequiredArgsConstructor
public class ScrapController {

    private final ScrapService scrapService;

    @PostMapping
    public ResponseEntity<ApiResponse> activeScrap(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                  @RequestBody @Valid ScrapRequestDto scrapRequestDto) {

        Long userId = userPrincipal.getId();

        scrapService.likeScrap(userId, scrapRequestDto);

        return ResponseEntity.ok(ApiResponse.success("게시글 스크랩 완료", null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deactiveScrap(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                    @RequestBody @Valid ScrapRequestDto scrapRequestDto) {

        Long userId = userPrincipal.getId();

        scrapService.unlikeScrap(userId, scrapRequestDto);

        return ResponseEntity.ok(ApiResponse.success("게시글 스크랩 취소 완료", null));
    }
}
