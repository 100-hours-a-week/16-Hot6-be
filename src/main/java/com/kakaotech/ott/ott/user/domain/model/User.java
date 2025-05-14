package com.kakaotech.ott.ott.user.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
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

    public static User createUser(String email, String nicknameCommunity, String imagePath) {
        return User.builder()
                .email(email)
                .role(Role.USER)
                .nicknameKakao(null)
                .nicknameCommunity(nicknameCommunity)
                .point(1000)
                .imagePath(imagePath)
                .isActive(true)
                .isVerified(false)
                .aiImageGeneratedDate(null)
                .build();
    }

    public void renewGeneratedDate() {
        this.aiImageGeneratedDate = LocalDate.now();
    }

    public void updatePoint(int point) {
        this.point += point;
    }

    public void updateProfileImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void updateNicknameCommunity(String nicknameCommunity) {
        this.nicknameCommunity = nicknameCommunity;
    }

    public void updateNicknameKakao(String nicknameKakao) {
        this.nicknameKakao = nicknameKakao;
    }

    public void updateActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void updateDeletedAt(LocalDateTime currentTime) {
        this.deletedAt = currentTime;
    }

    public void updateVerified() {
        this.isVerified = true;
    }

}