package com.kakaotech.ott.ott.auth.presentation;

import com.kakaotech.ott.ott.auth.application.JwtService;
import com.kakaotech.ott.ott.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

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

        return ResponseEntity.ok(ApiResponse.success("액세스 토큰 재발급 완료", newAccessToken));
    }
}