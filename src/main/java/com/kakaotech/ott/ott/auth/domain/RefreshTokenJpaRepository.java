package com.kakaotech.ott.ott.auth.domain;

import com.kakaotech.ott.ott.auth.infrastructure.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {

}
