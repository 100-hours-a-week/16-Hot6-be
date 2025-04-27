package com.kakaotech.ott.ott.user.domain;

import com.kakaotech.ott.ott.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    public static User createUser(String email, String nicknameCommunity, String imagePath) {

        return User.builder()
                .email(email)
                .role(Role.USER)
                .nicknameCommunity(nicknameCommunity)
                .point(0)
                .imagePath(imagePath)
                .isActive(true)
                .isVerified(false)
                .build();
    }

    public static UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .nicknameKakao(user.getNicknameKakao())
                .nicknameCommunity(user.getNicknameCommunity())
                .point(user.getPoint())
                .imagePath(user.getImagePath())
                .isActive(user.isActive())
                .isVerified(user.isVerified())
                .deletedAt(user.getDeletedAt())
                .build();
    }

}
