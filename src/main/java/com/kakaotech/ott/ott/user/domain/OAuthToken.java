package com.kakaotech.ott.ott.user.domain;

import com.kakaotech.ott.ott.user.entity.OAuthTokenEntity;
import com.kakaotech.ott.ott.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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

    @Builder
    public OAuthToken(Long id, Long userId, String provider, String providerId,
                      String accessToken, String refreshToken,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.provider = provider;
        this.providerId = providerId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static OAuthToken createOauthToken(Long userId, String provider, String providerId, String accessToken,
                                              String refreshToken) {

        return OAuthToken.builder()
                .userId(userId)
                .provider(provider)
                .providerId(providerId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public OAuthTokenEntity toEntity(UserEntity userEntity) {
        return OAuthTokenEntity.builder()
                .id(this.getId())
                .userEntity(userEntity)
                .provider(this.getProvider())
                .providerId(this.getProviderId())
                .accessToken(this.getAccessToken())
                .refreshToken(this.getRefreshToken())
                .build();
    }
}
