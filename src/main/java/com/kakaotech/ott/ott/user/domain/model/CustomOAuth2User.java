package com.kakaotech.ott.ott.user.domain.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final Long userId;
    private final String email;
    private final String nickname;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNicknameCommunity(); // 혹은 nicknameKakao
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한이 필요 없을 경우 빈 리스트로 반환
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return String.valueOf(userId); // Principal name → 사용자 식별자
    }
}
