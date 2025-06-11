package com.kakaotech.ott.ott.product.infrastructure.entity;

import com.kakaotech.ott.ott.product.domain.model.ProductImage;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_product_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProductImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상품과의 관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity productEntity;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    @Column(name = "image_uuid", nullable = false, length = 255)
    private String imageUuid;

    // Domain → Entity 변환
    public static ProductImageEntity from(ProductImage image, ProductEntity productEntity) {
        return ProductImageEntity.builder()
                .id(image.getId())
                .productEntity(productEntity)
                .sequence(image.getSequence())
                .imageUuid(image.getImageUuid())
                .build();
    }

    // Entity → Domain 변환
    public ProductImage toDomain() {
        return ProductImage.builder()
                .id(this.id)
                .productId(this.productEntity.getId())
                .sequence(this.sequence)
                .imageUuid(this.imageUuid)
                .build();
    }

}