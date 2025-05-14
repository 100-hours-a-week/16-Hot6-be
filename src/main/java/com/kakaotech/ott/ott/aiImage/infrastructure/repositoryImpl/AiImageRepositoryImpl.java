package com.kakaotech.ott.ott.aiImage.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImageState;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageJpaRepository;
import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageRepository;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.user.domain.repository.UserJpaRepository;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        AiImageEntity aiImageEntity = aiImageJpaRepository.findById(aiImage.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.AIIMAGE_NOT_FOUND));

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
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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
    public Slice<AiImage> findUserDeskImages(Long userId, Long cursorId, int size, String type) {

        if (type != null) {
            Slice<AiImageEntity> slice = aiImageJpaRepository.findUnlinkedByUserWithCursor(
                    userId, cursorId, PageRequest.of(0, size)
            );

            return slice.map(AiImageEntity::toDomain);
        }

        Slice<AiImageEntity> slice = aiImageJpaRepository.findByUserWithCursor(
                userId, cursorId, PageRequest.of(0, size)
        );

        return slice.map(AiImageEntity::toDomain);
    }

    @Override
    public AiImage findByBeforeImagePath(String beforeImagePath) {
        return aiImageJpaRepository.findByBeforeImagePath(beforeImagePath)
                .orElseThrow(() -> new CustomException(ErrorCode.AIIMAGE_NOT_FOUND))
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

    @Override
    public AiImage findByPostId(Long postId) {
        return aiImageJpaRepository.findByPostId(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.AIIMAGE_NOT_FOUND))
                .toDomain();
    }
}
