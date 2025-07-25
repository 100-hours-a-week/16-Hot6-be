package com.kakaotech.ott.ott.user.infrastructure.entity;

import com.kakaotech.ott.ott.user.domain.model.Role;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.util.AuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class UserEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Role role;

    @Column(name = "nickname_kakao", length = 20)
    private String nicknameKakao;

    @Column(name = "nickname_community", nullable = false, length = 20)
    private String nicknameCommunity;

    @Column(name = "image_path", nullable = false)
    private String imagePath;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder.Default
    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OAuthTokenEntity> oauthTokens = new ArrayList<>();

    public User toDomain() {
        return User.builder()
                .id(this.id)
                .email(this.email)
                .role(this.role)
                .nicknameKakao(this.nicknameKakao)
                .nicknameCommunity(this.nicknameCommunity)
                .imagePath(this.imagePath)
                .isActive(this.isActive)
                .isVerified(this.isVerified)
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .deletedAt(this.deletedAt)
                .build();
    }

    public static UserEntity from(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .nicknameKakao(user.getNicknameKakao())
                .nicknameCommunity(user.getNicknameCommunity())
                .imagePath(user.getImagePath())
                .isActive(user.isActive())
                .isVerified(user.isVerified())
                .deletedAt(user.getDeletedAt())
                .build();
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

    public void updateDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void updateVerified(String nicknameKakao) {
        this.isVerified = true;
        this.nicknameKakao = nicknameKakao;
    }

}
