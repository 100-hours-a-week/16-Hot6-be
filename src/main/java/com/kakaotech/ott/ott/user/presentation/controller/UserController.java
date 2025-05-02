package com.kakaotech.ott.ott.user.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/auth/{provider}")
    public ResponseEntity<Void> login(@PathVariable String provider, Authentication authentication) {
        // ✅ 이미 로그인된 사용자라면 홈으로 리디렉트
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {
            // `anonymousUser` 제외 체크
            return ResponseEntity.status(302)
                    .header("Location", "/") // 또는 /mypage 등
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
}
