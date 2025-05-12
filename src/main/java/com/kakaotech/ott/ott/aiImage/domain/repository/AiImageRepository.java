package com.kakaotech.ott.ott.aiImage.domain.repository;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AiImageRepository {

    AiImage savePost(AiImage aiImage);

    AiImage saveImage(AiImage aiImage);

    Optional<AiImageEntity> findById(Long userId);

    Slice<AiImage> findUserDeskImages(Long userId, Long cursorId, int size);

    AiImage findByBeforeImagePath(String beforeImagePath);

    Map<Long, AiImage> findByPostIds(List<Long> postIds);

    AiImage findByPostId(Long postId);
}
