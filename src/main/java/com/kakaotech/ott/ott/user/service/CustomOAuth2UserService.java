package com.kakaotech.ott.ott.user.service;


import com.kakaotech.ott.ott.user.domain.CustomOAuth2User;
import com.kakaotech.ott.ott.user.domain.OAuthToken;
import com.kakaotech.ott.ott.user.domain.User;
import com.kakaotech.ott.ott.user.repository.OAuthTokenRepository;
import com.kakaotech.ott.ott.user.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final OAuthTokenRepository oAuthTokenRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");
        String profileImageUrl = (profile != null && profile.get("profile_image_url") != null)
                ? (String) profile.get("profile_image_url")
                : "";

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .nicknameCommunity(nickname)
                        .imagePath(profileImageUrl)
                        .nicknameKakao("default")
                        .build()));

        // 2. OAuthToken 저장
        if (user != null) {
            String provider = "kakao"; // 고정 값
            String providerId = attributes.get("id").toString(); // 카카오 유저 ID
            String accessToken = userRequest.getAccessToken().getTokenValue();
            String refreshToken = null; // 카카오는 refresh_token 발급 안 해줄 수도 있음

            OAuthToken oauthToken = OAuthToken.createOauthToken(
                    user.getId(),
                    provider,
                    providerId,
                    accessToken,
                    refreshToken
            );

            oAuthTokenRepository.save(oauthToken); // ✅ 저장
        }


        return new CustomOAuth2User(user);
    }
}