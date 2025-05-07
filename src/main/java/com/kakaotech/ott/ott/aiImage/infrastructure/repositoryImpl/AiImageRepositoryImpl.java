package com.kakaotech.ott.ott.aiImage.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageJpaRepository;
import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageRepository;
import com.kakaotech.ott.ott.user.domain.repository.UserJpaRepository;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AiImageRepositoryImpl implements AiImageRepository {

    private final AiImageJpaRepository aiImageJpaRepository;
    private final UserJpaRepository userJpaRepository;


    @Override
    public AiImageEntity save(AiImage aiImage) {

        UserEntity userEntity = userJpaRepository.findById(aiImage.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 없습니다."));

        AiImageEntity aiImageEntity = (aiImage.getId() != null)
                ? aiImageJpaRepository.findById(aiImage.getId())
                .orElseThrow(() -> new EntityNotFoundException("AI 이미지 없음"))
                : AiImageEntity.from(aiImage, userEntity);

        // 3) postId 변경 반영
        aiImageEntity.setPostId(aiImage.getPostId());

        return aiImageJpaRepository.save(aiImageEntity);
    }

    @Override
    public Optional<AiImageEntity> findById(Long userId) {

        return aiImageJpaRepository.findById(userId);
    }
}
