package com.kakaotech.ott.ott.user.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.user.domain.repository.RefreshTokenJpaRepository;
import com.kakaotech.ott.ott.user.domain.repository.RefreshTokenRepository;
import com.kakaotech.ott.ott.user.infrastructure.entity.RefreshTokenEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public Optional<RefreshTokenEntity> findById(Long userId) {
        return refreshTokenJpaRepository.findById(userId);
    }

    @Override
    public RefreshTokenEntity save(RefreshTokenEntity refreshTokenEntity) {
        return refreshTokenJpaRepository.save(refreshTokenEntity);
    }

    @Override
    public Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken) {
        return refreshTokenJpaRepository.findByRefreshToken(refreshToken);
    }

    @Override
    public void delete(Long userId) {
        refreshTokenJpaRepository.deleteById(userId);
    }

    @Override
    public void deleteByRefreshToken(String refreshToken) {
        refreshTokenJpaRepository.deleteByRefreshToken(refreshToken);
    }

}
