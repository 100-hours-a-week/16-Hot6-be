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
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AiImageRepositoryImpl implements AiImageRepository {

    private final AiImageJpaRepository aiImageJpaRepository;
    private final UserJpaRepository userJpaRepository;


    @Override
    @Transactional
    public AiImage save(AiImage aiImage) {

        // ✅ 1. 기존 Entity 조회 (영속 상태)
        AiImageEntity aiImageEntity = aiImageJpaRepository.findByBeforeImagePath(aiImage.getBeforeImagePath())
                .orElse(null);

        if (aiImageEntity == null) {
            // ✅ 2. 이미지가 없는 경우 새로 생성
            UserEntity userEntity = userJpaRepository.findById(aiImage.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));

            aiImageEntity = AiImageEntity.builder()
                    .userEntity(userEntity)
                    .beforeImagePath(aiImage.getBeforeImagePath())
                    .afterImagePath(aiImage.getAfterImagePath())
                    .build();
            aiImageJpaRepository.save(aiImageEntity);
        } else {
            // ✅ 3. 이미지가 있는 경우 업데이트 (Dirty Checking)
            aiImageEntity.setAfterImagePath(aiImage.getAfterImagePath());
        }

        return aiImageEntity.toDomain();
    }

    @Override
    public Optional<AiImageEntity> findById(Long userId) {

        return aiImageJpaRepository.findById(userId);
    }

    @Override
    public AiImage findByBeforeImagePath(String beforeImagePath) {
        return aiImageJpaRepository.findByBeforeImagePath(beforeImagePath)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자의 데스크 사진이 존재하지 않습니다."))
                .toDomain();
    }
}
