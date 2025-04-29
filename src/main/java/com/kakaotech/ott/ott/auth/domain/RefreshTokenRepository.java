package com.kakaotech.ott.ott.auth.domain;

import com.kakaotech.ott.ott.auth.infrastructure.RefreshTokenEntity;

import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshTokenEntity> findById(Long userId);

    RefreshTokenEntity save(RefreshTokenEntity refreshTokenEntity);
}
