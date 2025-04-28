package com.kakaotech.ott.ott.user.repository;

import com.kakaotech.ott.ott.user.entity.OAuthTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthTokenJpaRepository extends JpaRepository<OAuthTokenEntity, Long> {
}
