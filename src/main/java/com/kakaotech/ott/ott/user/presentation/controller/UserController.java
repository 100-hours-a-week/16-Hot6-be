package com.kakaotech.ott.ott.user.presentation.controller;

import com.kakaotech.ott.ott.user.application.service.JwtService;
import com.kakaotech.ott.ott.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private final JwtService jwtService;


    @GetMapping("/auth/{provider}")
    public ResponseEntity<Void> login(@PathVariable String provider, Authentication authentication) {
        // ✅ 이미 로그인된 사용자라면 홈으로 리디렉트
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "https://dev.onthe-top.com/")
                    .build();
        }

        // ✅ 로그인 안 된 경우에만 OAuth2 로그인 흐름 시작
        String redirectUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/oauth2/authorization/")
                .path(provider)
                .build()
                .toUriString();

        return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String bearerToken) {
        String token = bearerToken.substring(7); // "Bearer " 떼기
        Long userId = jwtService.extractUserId(token);

        jwtService.logout(userId);

        return ResponseEntity.ok(ApiResponse.success("로그아웃 완료", null));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<String>> reissue(@RequestHeader("Authorization") String bearerToken) {
        String refreshToken = bearerToken.substring(7); // Bearer 부분 떼고
        String newAccessToken = jwtService.reissueAccessToken(refreshToken);

        return ResponseEntity.ok(ApiResponse.success("Access Token 재발급 성공", newAccessToken));
    }
}
