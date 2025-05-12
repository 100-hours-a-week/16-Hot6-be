package com.kakaotech.ott.ott.user.presentation.controller;

import com.kakaotech.ott.ott.user.application.serviceImpl.JwtService;
import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.user.application.service.UserAuthService;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import com.kakaotech.ott.ott.user.presentation.dto.response.RefreshTokenResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserAuthController {

    private final JwtService jwtService;
    private final UserAuthService userAuthService;

    @GetMapping("/{provider}")
    public ResponseEntity<Void> login(@PathVariable String provider,
                                      @AuthenticationPrincipal UserPrincipal userPrincipal,
                                      @RequestHeader(value = "X-Forwarded-Host", required = false) String forwardedHost) {

        System.out.println("userPrincipal : " + userPrincipal);

        // 이미 로그인된 사용자라면 홈으로 리디렉트
        if (userPrincipal != null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "https://dev.onthe-top.com/")
                    .build();
        }


        // 리디렉트 URL을 고정된 주소로 지정
        String redirectUrl = "https://dev-backend.onthe-top.com/oauth2/authorization/" + provider;

        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, redirectUrl)
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    @RequestHeader("Authorization") String bearerToken) {
        // ✅ JWT Access Token 확인
        String accessToken = bearerToken.substring(7); // "Bearer " 떼기
        Long userId = jwtService.extractUserId(accessToken);

        // ✅ 클라이언트에서 카카오 Access Token 전달받기
        String kakaoAccessToken = request.getHeader("Kakao-Access-Token");

        // ✅ 서비스 계층에서 로그아웃 처리
        userAuthService.logout(userId, response, kakaoAccessToken);


        return ResponseEntity.ok(ApiResponse.success("로그아웃 완료", null));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponseDto>> reissue(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {

        if (refreshToken == null) {
            throw new AccessDeniedException("Refresh Token 누락");
        }

        String newAccessToken = jwtService.reissueAccessToken(refreshToken);

        RefreshTokenResponseDto refreshTokenResponseDto = new RefreshTokenResponseDto(newAccessToken);
        return ResponseEntity.ok(ApiResponse.success("Access Token 재발급 성공", refreshTokenResponseDto));
    }
}
