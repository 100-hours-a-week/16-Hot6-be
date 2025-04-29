package com.kakaotech.ott.ott.user.domain.service;

import com.kakaotech.ott.ott.user.domain.model.CustomOAuth2User;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.OAuthTokenRepository;
import com.kakaotech.ott.ott.user.infrastructure.OAuthTokenEntity;
import com.kakaotech.ott.ott.user.infrastructure.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserDomainService userDomainService;
    private final OAuthTokenRepository oAuthTokenRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 카카오에서 사용자 정보 조회
        OAuth2User oauth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oauth2User.getAttributes();

        // 사용자 저장 또는 조회 (도메인 서비스)
        User user = userDomainService.saveOrGetKakaoUser(attributes);

        // 카카오 토큰 정보 가져오기
        String accessToken = userRequest.getAccessToken().getTokenValue();
        String providerId = attributes.get("id").toString();

        // access/refresh token 저장 또는 갱신
        OAuthTokenEntity tokenEntity = oAuthTokenRepository.findByUserId(user.getId())
                .map(entity -> {
                    entity.updateAccessToken(accessToken); // 기존 값 갱신
                    return entity;
                })
                .orElseGet(() -> OAuthTokenEntity.builder()
                        .provider("kakao")
                        .providerId(providerId)
                        .accessToken(accessToken)
                        .userEntity(UserEntity.from(user))
                        .build());

        oAuthTokenRepository.save(tokenEntity);

        // CustomOAuth2User 반환 (JWT 발급할 때 사용됨)
        return new CustomOAuth2User(user, attributes);
    }
}
