package com.kakaotech.ott.ott.aiImage.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.aiImage.domain.model.ImageGenerationHistory;
import com.kakaotech.ott.ott.aiImage.domain.repository.ImageGenerationHistoryJpaRepository;
import com.kakaotech.ott.ott.aiImage.domain.repository.ImageGenerationHistoryRepository;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.ImageGenerationHistoryEntity;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class ImageGenerationHistoryRepositoryImpl implements ImageGenerationHistoryRepository {

    private final ImageGenerationHistoryJpaRepository imageGenerationHistoryJpaRepository;

    @Override
    public int checkGenerationTokenCount(Long userId, LocalDate dateKey) {

        return imageGenerationHistoryJpaRepository.countByUserEntity_IdAndDateKey(userId, dateKey);
    }

    @Override
    public ImageGenerationHistory save(ImageGenerationHistory imageGenerationHistory, User user) {

        ImageGenerationHistoryEntity imageGenerationHistoryEntity = ImageGenerationHistoryEntity.from(imageGenerationHistory, UserEntity.from(user));

        return imageGenerationHistoryJpaRepository.save(imageGenerationHistoryEntity).toDomain();
    }
}
