package com.kakaotech.ott.ott.user.presentation.controller;

import com.kakaotech.ott.ott.user.application.serviceImpl.JwtService;
import com.kakaotech.ott.ott.user.domain.model.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = customUser.getUserId();

        // 1. JWT 발급
        String refreshToken = jwtService.createRefreshToken(userId);

        // 2. Refresh Token 저장 (DB or Redis)
        jwtService.storeRefreshToken(userId, refreshToken);

        // 3. Refresh Token을 HttpOnly, Secure 쿠키로 저장
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain("dev.onthe-top.com") // 환경에 맞게 도메인 설정
                .path("/")
                .maxAge(Duration.ofDays(7))  // 7일 유효
                .build();
        response.addHeader("Set-Cookie", refreshCookie.toString());

        // 4. 로그인 성공 후 클라이언트로 리디렉트
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader(HttpHeaders.LOCATION, "https://dev.onthe-top.com/oauth-success");
    }
}