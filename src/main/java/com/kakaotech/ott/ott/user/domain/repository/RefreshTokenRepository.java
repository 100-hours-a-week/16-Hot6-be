package com.kakaotech.ott.ott.user.domain.repository;

import com.kakaotech.ott.ott.user.infrastructure.entity.RefreshTokenEntity;

import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshTokenEntity> findById(Long userId);

    RefreshTokenEntity save(RefreshTokenEntity refreshTokenEntity);

    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);

    void delete(Long userId);

    void deleteByRefreshToken(String refreshToken);
}
