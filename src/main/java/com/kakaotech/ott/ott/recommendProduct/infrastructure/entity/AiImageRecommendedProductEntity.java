package com.kakaotech.ott.ott.recommendProduct.infrastructure.entity;

import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.recommendProduct.domain.model.AiImageRecommendedProduct;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ai_image_recommended_products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class AiImageRecommendedProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_image_id", nullable = false)
    private AiImageEntity aiImageEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "desk_product_id", nullable = false)
    private DeskProductEntity deskProductEntity;

    @Column(name = "center_x")
    private Integer centerX;

    @Column(name = "center_y")
    private Integer centerY;

    public static AiImageRecommendedProductEntity from(AiImageRecommendedProduct aiImageRecommendedProduct, AiImageEntity aiImageEntity, DeskProductEntity deskProductEntity) {

        return AiImageRecommendedProductEntity.builder()
                .aiImageEntity(aiImageEntity)
                .deskProductEntity(deskProductEntity)
                .centerX(aiImageRecommendedProduct.getCenterX())
                .centerY(aiImageRecommendedProduct.getCenterY())
                .build();
    }

    public AiImageRecommendedProduct toDomain() {

        return AiImageRecommendedProduct.builder()
                .id(this.getId())
                .aiImageId(this.aiImageEntity.getId())
                .deskProductId(this.deskProductEntity.getId())
                .centerX(this.getCenterX())
                .centerY(this.getCenterY())
                .build();
    }
}
