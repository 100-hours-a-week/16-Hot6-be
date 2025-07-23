package com.kakaotech.ott.ott.scrap.presentation.controller;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.scrap.application.service.ScrapService;
import com.kakaotech.ott.ott.scrap.presentation.dto.request.ScrapRequestDto;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/scraps")
@RequiredArgsConstructor
public class ScrapController {

    private final ScrapService scrapService;

    @PostMapping
    public ResponseEntity<ApiResponse> toggleScrap(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                   @RequestBody @Valid ScrapRequestDto scrapRequestDto) {

        Long userId = userPrincipal.getId();

        scrapService.toggleScrap(userId, scrapRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("스크랩 완료", null));
    }
}
