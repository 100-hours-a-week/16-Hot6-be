package com.kakaotech.ott.ott.home.presentation.controller;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.home.presentation.dto.response.MainResponseDto;
import com.kakaotech.ott.ott.post.application.service.PostService;
import com.kakaotech.ott.ott.product.application.service.ProductDomainService;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HomeController {

    private final PostService postService;
    private final ProductDomainService productDomainService;

    @GetMapping("/main")
    public ResponseEntity<ApiResponse<MainResponseDto>> home(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Long userId = (userPrincipal == null) ? null : userPrincipal.getId();

        MainResponseDto mainResponseDto = new MainResponseDto(
                postService.getPopularSetups(userId),
                productDomainService.getRecommendItems(userId)
        );

        return ResponseEntity.ok(ApiResponse.success("랜딩페이지 조회 성공", mainResponseDto));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse> serverCheck() {

        return ResponseEntity.ok(ApiResponse.success("Server On", null));
    }

}
