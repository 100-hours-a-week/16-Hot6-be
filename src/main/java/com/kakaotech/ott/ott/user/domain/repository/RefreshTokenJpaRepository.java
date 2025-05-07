package com.kakaotech.ott.ott.user.domain.repository;

import com.kakaotech.ott.ott.user.infrastructure.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {

}
