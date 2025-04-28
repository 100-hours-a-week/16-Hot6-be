package com.kakaotech.ott.ott.user.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final User user;

    public CustomOAuth2User(User user) {
        this.user = user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of(); // 별도로 필요 없으면 빈 Map 리턴
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // 필요 시 ROLE 설정 가능
    }

    @Override
    public String getName() {
        return user.getId().toString();
    }
}