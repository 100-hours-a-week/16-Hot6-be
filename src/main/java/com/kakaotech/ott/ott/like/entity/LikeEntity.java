package com.kakaotech.ott.ott.like.entity;

import com.kakaotech.ott.ott.like.domain.Like;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "likes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "type", nullable = false, length = 30)
    private String type;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Like toDomain() {
        return Like.builder()
                .id(this.id)
                .userId(this.userId)
                .type(this.type)
                .targetId(this.targetId)
                .isActive(this.isActive)
                .createdAt(this.createdAt)
                .build();
    }
}