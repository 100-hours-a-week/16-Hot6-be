package com.kakaotech.ott.ott.user.infrastructure.repository;

import com.kakaotech.ott.ott.user.domain.repository.OAuthTokenRepository;
import com.kakaotech.ott.ott.user.infrastructure.entity.OAuthTokenEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OAuthTokenRepositoryImpl implements OAuthTokenRepository {

    private final OAuthTokenJpaRepository oAuthTokenJpaRepository;

    @Override
    public Optional<OAuthTokenEntity> findByUserId(Long userId) {
        return oAuthTokenJpaRepository.findByUserEntityId(userId);
    }

    @Override
    public OAuthTokenEntity save(OAuthTokenEntity token) {
        return oAuthTokenJpaRepository.save(token);
    }
}
