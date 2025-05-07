package com.kakaotech.ott.ott.user.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaotech.ott.ott.user.application.service.JwtService;
import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.user.domain.model.CustomOAuth2User;
import com.kakaotech.ott.ott.user.presentation.dto.response.OAuthLoginResponseDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = customUser.getUserId();

        // 1. JWT 발급
        String accessToken = jwtService.createAccessToken(userId);
        String refreshToken = jwtService.createRefreshToken(userId);

        // 2. Refresh Token 저장 (DB or Redis)
        jwtService.storeRefreshToken(userId, refreshToken);

        // 3. Access Token을 HttpOnly, Secure 쿠키로 저장
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)              // 클라이언트에서 접근 불가
                .secure(true)               // HTTPS에서만 동작
                .sameSite("None")           // 크로스 도메인 쿠키 허용
                .domain(".onthe-top.com")   // 최상위 도메인
                .path("/")
                .maxAge(30 * 60)            // 30분 유효 (Access Token 만료 시간과 맞춤)
                .build();
        response.addHeader("Set-Cookie", accessCookie.toString());

        // 4. Refresh Token을 HttpOnly, Secure 쿠키로 저장
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(".onthe-top.com")
                .path("/")
                .maxAge(7 * 24 * 60 * 60)  // 7일 유효
                .build();
        response.addHeader("Set-Cookie", refreshCookie.toString());

        // 5. 클라이언트를 홈 페이지로 리디렉션
        response.sendRedirect("https://dev.onthe-top.com/");
    }
}