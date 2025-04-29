package com.kakaotech.ott.ott.user.domain.repository;

import com.kakaotech.ott.ott.user.infrastructure.OAuthTokenEntity;

import java.util.Optional;

public interface OAuthTokenRepository {

    Optional<OAuthTokenEntity> findByUserId(Long userId);

    OAuthTokenEntity save(OAuthTokenEntity token);
}
