package com.kakaotech.ott.ott.aiImage.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImageState;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageJpaRepository;
import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageRepository;
import com.kakaotech.ott.ott.user.domain.repository.UserJpaRepository;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AiImageRepositoryImpl implements AiImageRepository {

    private final AiImageJpaRepository aiImageJpaRepository;
    private final UserJpaRepository userJpaRepository;


    @Override
    @Transactional
    public AiImage savePost(AiImage aiImage) {

        UserEntity userEntity = userJpaRepository.findById(aiImage.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));

        AiImageEntity aiImageEntity = aiImageJpaRepository.findById(aiImage.getId())
                .orElseThrow(() -> new EntityNotFoundException("해당 AI 이미지가 존재하지 않습니다."));

        aiImageEntity.setPostId(aiImage.getPostId());

        return aiImageEntity.toDomain();
    }

    @Override
    @Transactional
    public AiImage saveImage(AiImage aiImage) {

        // ✅ 1. 기존 Entity 조회 (영속 상태)
        AiImageEntity aiImageEntity = aiImageJpaRepository.findByBeforeImagePath(aiImage.getBeforeImagePath())
                .orElse(null);

        if (aiImageEntity == null) {
            // ✅ 2. 이미지가 없는 경우 새로 생성
            UserEntity userEntity = userJpaRepository.findById(aiImage.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));

            aiImageEntity = AiImageEntity.builder()
                    .userEntity(userEntity)
                    .state(aiImage.getState())
                    .beforeImagePath(aiImage.getBeforeImagePath())
                    .afterImagePath(aiImage.getAfterImagePath())
                    .build();
            aiImageJpaRepository.save(aiImageEntity);
        } else { // ✅ 3. 이미지가 있는 경우 상태 업데이트 (Dirty Checking)
            aiImageEntity.setState(aiImage.getState());

            if (aiImage.getState().equals(AiImageState.SUCCESS)) {
                aiImageEntity.setAfterImagePath(aiImage.getAfterImagePath());
            } else if (aiImage.getState().equals(AiImageState.FAILED)) {
                aiImageEntity.setAfterImagePath(null);
            }

            aiImageJpaRepository.saveAndFlush(aiImageEntity); // ✅ Dirty Checking 강제 반영
        }

        return aiImageEntity.toDomain();
    }

    @Override
    public Optional<AiImageEntity> findById(Long userId) {

        return aiImageJpaRepository.findById(userId);
    }

    @Override
    public Slice<AiImage> findUserDeskImages(Long userId, LocalDateTime cursorCreatedAt, Long cursorId, int size) {
        Slice<AiImageEntity> slice = aiImageJpaRepository.findByUserWithCursor(
                userId, cursorCreatedAt, cursorId, PageRequest.of(0, size)
        );

        return slice.map(AiImageEntity::toDomain);
    }

    @Override
    public AiImage findByBeforeImagePath(String beforeImagePath) {
        return aiImageJpaRepository.findByBeforeImagePath(beforeImagePath)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자의 데스크 사진이 존재하지 않습니다."))
                .toDomain();
    }

    @Override
    public Map<Long, AiImage> findByPostIds(List<Long> postIds) {
        List<AiImageEntity> entities = aiImageJpaRepository.findByPostIdIn(postIds);
        return entities.stream()
                .collect(Collectors.toMap(
                        AiImageEntity::getPostId,
                        AiImageEntity::toDomain
                ));
    }
}
