package com.kakaotech.ott.ott.user.domain;

import com.kakaotech.ott.ott.user.entity.OAuthTokenEntity;
import com.kakaotech.ott.ott.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class OAuthToken {

    private Long id;

    private Long userId;

    private String provider;

    private String providerId;

    private String accessToken;

    private String refreshToken;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static OAuthToken createOauthToken(Long userId, String provider, String providerId, String accessToken, String refreshToken) {

        return OAuthToken.builder()
                .userId(userId)
                .provider(provider)
                .providerId(providerId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static OAuthTokenEntity toEntity(OAuthToken oAuthToken, UserEntity userEntity) {
        return OAuthTokenEntity.builder()
                .id(oAuthToken.getId())
                .userEntity(userEntity)
                .provider(oAuthToken.getProvider())
                .providerId(oAuthToken.getProviderId())
                .accessToken(oAuthToken.getAccessToken())
                .refreshToken(oAuthToken.getRefreshToken())
                .build();
    }
}
