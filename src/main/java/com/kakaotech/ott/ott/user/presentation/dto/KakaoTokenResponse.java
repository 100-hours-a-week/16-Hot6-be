package com.kakaotech.ott.ott.user.presentation.dto;

import lombok.Getter;

@Getter
public class KakaoTokenResponse {
    private String accessToken;
    private String tokenType;
    private String refreshToken;
    private int expiresIn;
    private String scope;
}

