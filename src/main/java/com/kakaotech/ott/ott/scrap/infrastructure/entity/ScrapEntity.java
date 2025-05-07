package com.kakaotech.ott.ott.scrap.infrastructure.entity;

import com.kakaotech.ott.ott.scrap.domain.model.Scrap;
import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "scraps")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ScrapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // FK 컬럼명
    private UserEntity userEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private ScrapType type;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Scrap toDomain() {
        return Scrap.builder()
                .id(this.id)
                .userId(this.userEntity.getId())
                .type(this.type)
                .targetId(this.targetId)
                .isActive(this.isActive)
                .createdAt(this.createdAt)
                .build();
    }

    public static ScrapEntity from(Scrap scrap, UserEntity userEntity) {
        return ScrapEntity.builder()
                .id(scrap.getId())
                .userEntity(userEntity)
                .type(scrap.getType())
                .targetId(scrap.getTargetId())
                .isActive(scrap.getIsActive())
                .createdAt(scrap.getCreatedAt())
                .build();
    }
}
