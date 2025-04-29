package com.kakaotech.ott.ott.user.infrastructure;

import com.kakaotech.ott.ott.util.AuditEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "oauth_tokens")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 개발자가 new로 객체 생성하는 것을 막기 위해, JPA만 접근 가능
@AllArgsConstructor
@Builder
public class OAuthTokenEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "access_token")
    private String accessToken;

    public void updateAccessToken(String newAccessToken) {
        this.accessToken = newAccessToken;
    }
}
