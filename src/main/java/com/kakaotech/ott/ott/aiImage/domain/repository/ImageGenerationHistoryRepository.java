package com.kakaotech.ott.ott.aiImage.domain.repository;

import com.kakaotech.ott.ott.aiImage.domain.model.ImageGenerationHistory;
import com.kakaotech.ott.ott.user.domain.model.User;

import java.time.LocalDate;

public interface ImageGenerationHistoryRepository {

    int checkGenerationTokenCount(Long userId, LocalDate dateKey);

    ImageGenerationHistory save(ImageGenerationHistory imageGenerationHistory, User user);
}
