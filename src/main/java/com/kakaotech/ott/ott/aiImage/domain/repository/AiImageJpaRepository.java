package com.kakaotech.ott.ott.aiImage.domain.repository;

import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiImageJpaRepository extends JpaRepository<AiImageEntity, Long> {

    Optional<AiImageEntity> findByBeforeImagePath(String beforeImagePath);
}
