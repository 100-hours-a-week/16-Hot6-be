package com.kakaotech.ott.ott.aiImage.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.aiImage.domain.repository.ImageGenerationHistoryJpaRepository;
import com.kakaotech.ott.ott.aiImage.domain.repository.ImageGenerationHistoryRepository;
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
}
