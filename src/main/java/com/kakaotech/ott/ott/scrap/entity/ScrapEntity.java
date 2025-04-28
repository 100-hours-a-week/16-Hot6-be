package com.kakaotech.ott.ott.scrap.entity;

import com.kakaotech.ott.ott.scrap.domain.Scrap;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "scraps")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ScrapEntity {

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

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Scrap toDomain() {
        return Scrap.builder()
                .id(this.id)
                .userId(this.userId)
                .type(this.type)
                .targetId(this.targetId)
                .isActive(this.isActive)
                .createdAt(this.createdAt)
                .build();
    }
}
