package com.kakaotech.ott.ott.user.domain.model;

import com.kakaotech.ott.ott.user.domain.Role;
import com.kakaotech.ott.ott.user.infrastructure.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {

    private final Long id;
    private final String email;
    private final Role role;
    private final String nicknameKakao;
    private final String nicknameCommunity;
    private final int point;
    private final String imagePath;
    private final boolean isActive;
    private final boolean isVerified;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;

    @Builder
    public User(Long id, String email, Role role, String nicknameKakao, String nicknameCommunity,
                int point, String imagePath, boolean isActive, boolean isVerified,
                LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.nicknameKakao = nicknameKakao;
        this.nicknameCommunity = nicknameCommunity;
        this.point = point;
        this.imagePath = imagePath;
        this.isActive = isActive;
        this.isVerified = isVerified;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static User createUser(String email, String nicknameCommunity, String imagePath) {
        return User.builder()
                .email(email)
                .role(Role.USER)
                .nicknameKakao(null)
                .nicknameCommunity(nicknameCommunity)
                .point(0)
                .imagePath(imagePath)
                .isActive(true)
                .isVerified(false)
                .build();
    }

}