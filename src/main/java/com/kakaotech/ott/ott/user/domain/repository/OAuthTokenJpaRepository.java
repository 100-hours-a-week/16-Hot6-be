package com.kakaotech.ott.ott.user.domain.repository;

import com.kakaotech.ott.ott.user.infrastructure.OAuthTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthTokenJpaRepository extends JpaRepository<OAuthTokenEntity, Long> {

    Optional<OAuthTokenEntity> findByUserEntityId(Long userId);
}
