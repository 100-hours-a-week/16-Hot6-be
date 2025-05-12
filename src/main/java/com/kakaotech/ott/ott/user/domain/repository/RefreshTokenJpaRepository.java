package com.kakaotech.ott.ott.user.domain.repository;

import com.kakaotech.ott.ott.user.infrastructure.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.refreshToken = :refreshToken")
    void deleteByRefreshToken(String refreshToken);
}
