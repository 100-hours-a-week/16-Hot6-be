package com.kakaotech.ott.ott.auth.infrastructure;

import com.kakaotech.ott.ott.auth.domain.RefreshTokenJpaRepository;
import com.kakaotech.ott.ott.auth.domain.RefreshTokenRepository;
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

}
