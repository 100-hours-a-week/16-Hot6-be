package com.kakaotech.ott.ott.user.presentation.controller;

import com.kakaotech.ott.ott.user.application.serviceImpl.JwtService;
import com.kakaotech.ott.ott.user.domain.model.CustomOAuth2User;
import com.kakaotech.ott.ott.user.domain.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.security.oauth2.redirectURL.front}")
    String baseURl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = customUser.getUserId();

        // ✅ Refresh Token 무조건 새로 생성
        String refreshToken = jwtService.createRefreshToken(userId);
        jwtService.storeRefreshToken(userId, refreshToken);

        // ✅ Refresh Token을 HttpOnly, Secure 쿠키로 저장
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None") // ✅ SameSite 설정
                .domain(".onthe-top.com")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

        // ✅ Spring ResponseCookie를 사용하여 쿠키 설정
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        // ✅ 로그인 성공 후 클라이언트로 리디렉트
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader(HttpHeaders.LOCATION, baseURl + "oauth-success");
    }


}