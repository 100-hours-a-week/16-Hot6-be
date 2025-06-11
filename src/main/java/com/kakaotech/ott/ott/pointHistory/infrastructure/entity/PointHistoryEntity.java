package com.kakaotech.ott.ott.pointHistory.infrastructure.entity;

import com.kakaotech.ott.ott.pointHistory.domain.model.PointActionReason;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointActionType;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointHistory;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "point_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PointHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // FK 컬럼명
    private UserEntity userEntity;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "balance_after", nullable = false)
    private int balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PointActionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "description", nullable = false)
    private PointActionReason description;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    public static PointHistoryEntity from(PointHistory pointHistory, UserEntity userEntity) {

        return PointHistoryEntity.builder()
                .userEntity(userEntity)
                .amount(pointHistory.getAmount())
                .balanceAfter(pointHistory.getBalanceAfter())
                .type(pointHistory.getType())
                .description(pointHistory.getDescription())
                .build();
    }

    public PointHistory toDomain() {

        return PointHistory.builder()
                .id(this.getId())
                .userId(this.getUserEntity().getId())
                .amount(this.getAmount())
                .balanceAfter(this.getBalanceAfter())
                .type(this.getType())
                .description(this.getDescription())
                .createdAt(this.getCreatedAt())
                .build();
    }
}
