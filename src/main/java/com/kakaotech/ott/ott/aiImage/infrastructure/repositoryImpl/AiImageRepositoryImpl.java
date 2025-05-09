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
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자의 데스크 사진이 존재하지 않습니다."));

        // ✅ 2. Entity의 값 변경 (Dirty Checking)
        aiImageEntity.setAfterImagePath(aiImage.getAfterImagePath());

        // ✅ 3. 변경 감지를 통해 자동으로 업데이트
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
