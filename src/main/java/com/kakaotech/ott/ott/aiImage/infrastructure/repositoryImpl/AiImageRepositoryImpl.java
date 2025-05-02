package com.kakaotech.ott.ott.aiImage.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageJpaRepository;
import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageRepository;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.infrastructure.repository.UserJpaRepository;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AiImageRepositoryImpl implements AiImageRepository {

    private final AiImageJpaRepository aiImageJpaRepository;
    private final UserJpaRepository userJpaRepository;


    @Override
    public AiImageEntity save(AiImage aiImage, User user) {

        return aiImageJpaRepository.save(AiImageEntity.from(aiImage, UserEntity.from(user)));
    }

    @Override
    public Optional<AiImageEntity> findById(Long userId) {

        return aiImageJpaRepository.findById(userId);
    }
}
