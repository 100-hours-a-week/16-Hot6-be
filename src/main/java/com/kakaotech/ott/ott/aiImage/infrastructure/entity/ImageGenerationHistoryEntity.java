package com.kakaotech.ott.ott.aiImage.infrastructure.entity;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImageConcept;
import com.kakaotech.ott.ott.aiImage.domain.model.ImageGenerationHistory;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "image_generation_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class ImageGenerationHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // FK 컬럼명
    private UserEntity userEntity;

    @Column(name = "date_key", nullable = false)
    private LocalDate dateKey;

    @Column(name = "prompt_summary")
    private AiImageConcept promptSummary;

    @CreatedDate
    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    public static ImageGenerationHistoryEntity from(ImageGenerationHistory imageGenerationHistory, UserEntity userEntity) {

        return ImageGenerationHistoryEntity.builder()
                .userEntity(userEntity)
                .dateKey(imageGenerationHistory.getDateKey())
                .promptSummary(imageGenerationHistory.getPromptSummary())
                .build();
    }

    public ImageGenerationHistory toDomain() {

        return ImageGenerationHistory.builder()
                .id(this.getId())
                .userId(this.getUserEntity().getId())
                .dateKey(this.getDateKey())
                .promptSummary(this.getPromptSummary())
                .generatedAt(this.getGeneratedAt())
                .build();
    }
}
