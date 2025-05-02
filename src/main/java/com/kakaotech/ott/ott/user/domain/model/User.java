package com.kakaotech.ott.ott.user.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class User {

    private Long id;
    private String email;
    private Role role;
    private String nicknameKakao;
    private String nicknameCommunity;
    private int point;
    private String imagePath;
    private boolean isActive;
    private boolean isVerified;
    private LocalDate aiImageGeneratedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @Builder
    public User(Long id, String email, Role role, String nicknameKakao, String nicknameCommunity,
                int point, String imagePath, boolean isActive, boolean isVerified,
                LocalDate aiImageGeneratedDate, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.nicknameKakao = nicknameKakao;
        this.nicknameCommunity = nicknameCommunity;
        this.point = point;
        this.imagePath = imagePath;
        this.isActive = isActive;
        this.isVerified = isVerified;
        this.aiImageGeneratedDate = aiImageGeneratedDate;
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
                .aiImageGeneratedDate(null)
                .build();
    }

    public void renewGeneratedDate() {
        this.aiImageGeneratedDate = LocalDate.now();
    }

}