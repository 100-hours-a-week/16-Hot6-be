package com.kakaotech.ott.ott.aiImage.domain.repository;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.user.domain.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AiImageRepository {

    AiImage savePost(AiImage aiImage);

    AiImage saveImage(AiImage aiImage);

    Optional<AiImageEntity> findById(Long userId);

    AiImage findByBeforeImagePath(String beforeImagePath);

    Map<Long, AiImage> findByPostIds(List<Long> postIds);
}
