package com.kakaotech.ott.ott.user.entity;

import com.kakaotech.ott.ott.user.domain.Role;
import com.kakaotech.ott.ott.user.domain.User;
import com.kakaotech.ott.ott.util.AuditEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)  // JPA가 엔티티 상태를 감지하고 어떤 리스너를 연결할지 설정하는 것으로 createdAt, updatedAt 자동 등록
@Table(name = "users")
@NoArgsConstructor
@Getter
public class UserEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private Role role;

    @Column(name = "nickname_kakao", nullable = false, length = 20)
    private String nicknameKakao;

    @Column(name = "nickname_community", nullable = false, length = 20)
    private String nicknameCommunity;

    @Column(name = "point", nullable = false)
    private int point;

    @Column(name = "image_path", nullable = false)
    private String imagePath;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @Column(name = "deleted_at", nullable = false)
    private LocalDateTime deletedAt;

    // UserEntity 삭제되면 OAuthEntity도 삭제
    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OAuthTokenEntity> oauthTokens = new ArrayList<>();

    @Builder
    public UserEntity(Long id, String email, Role role, String nicknameKakao, String nicknameCommunity, int point,
                      String imagePath, boolean isActive, boolean isVerified, LocalDateTime deletedAt) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.nicknameKakao = nicknameKakao;
        this.nicknameCommunity = nicknameCommunity;
        this.point = point;
        this.imagePath = imagePath;
        this.isActive = isActive;
        this.isVerified = isVerified;
        this.deletedAt = deletedAt;
    }

    public User toDomain() {
        return User.builder()
                .id(this.id)
                .email(this.email)
                .role(this.role)
                .nicknameKakao(this.nicknameKakao)
                .nicknameCommunity(this.nicknameCommunity)
                .point(this.point)
                .imagePath(this.imagePath)
                .isActive(this.isActive)
                .isVerified(this.isVerified)
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .deletedAt(this.deletedAt)
                .build();
    }
}
