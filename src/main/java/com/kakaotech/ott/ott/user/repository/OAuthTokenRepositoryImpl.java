package com.kakaotech.ott.ott.user.repository;

import com.kakaotech.ott.ott.user.domain.OAuthToken;
import com.kakaotech.ott.ott.user.domain.User;
import com.kakaotech.ott.ott.user.entity.OAuthTokenEntity;
import com.kakaotech.ott.ott.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthTokenRepositoryImpl implements OAuthTokenRepository{

    private final OAuthTokenJpaRepository oAuthTokenJpaRepository;
    private final UserRepository userRepository;

    @Override
    public OAuthToken save(OAuthToken oAuthToken) {

        UserEntity userEntity = userRepository.findById(oAuthToken.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        return oAuthTokenJpaRepository.save(oAuthToken.toEntity(userEntity)).toDomain();
    }
}
