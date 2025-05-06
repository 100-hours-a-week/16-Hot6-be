package com.kakaotech.ott.ott.aiImage.domain.repository;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.user.domain.model.User;

import java.util.Optional;

public interface AiImageRepository {

    AiImageEntity save(AiImage aiImage);

    Optional<AiImageEntity> findById(Long userId);
}
