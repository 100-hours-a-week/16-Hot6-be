package com.kakaotech.ott.ott.aiImage.domain.repository;

import com.kakaotech.ott.ott.aiImage.infrastructure.entity.ImageGenerationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ImageGenerationHistoryJpaRepository extends JpaRepository<ImageGenerationHistoryEntity, Long> {

    int countByUserEntity_IdAndDateKey(Long userId, LocalDate dateKey);
}
