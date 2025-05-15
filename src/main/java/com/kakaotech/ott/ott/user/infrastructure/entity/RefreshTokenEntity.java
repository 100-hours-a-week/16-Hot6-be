package com.kakaotech.ott.ott.user.infrastructure.entity;

import com.kakaotech.ott.ott.util.AuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenEntity extends AuditEntity {

    @Id
    private Long userId;

    @Column(nullable = false, length = 512)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime refreshTokenExpiration;

    public void updateRefreshToken(String refreshToken, LocalDateTime refreshTokenExpiration) {
        this.refreshToken = refreshToken;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // 만료 여부 확인 메서드
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.refreshTokenExpiration);
    }

}
