package com.kakaotech.ott.ott.aiImage.domain.repository;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiImageJpaRepository extends JpaRepository<AiImageEntity, Long> {

}
