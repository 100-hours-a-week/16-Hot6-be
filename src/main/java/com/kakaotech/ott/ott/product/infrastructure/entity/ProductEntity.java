package com.kakaotech.ott.ott.product.infrastructure.entity;

import com.kakaotech.ott.ott.product.domain.model.*;
import com.kakaotech.ott.ott.util.AuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "service_products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProductEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private ProductType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ProductStatus status;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    // JSON 타입 처리 (MySQL 8.0+)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "specification", nullable = false, columnDefinition = "JSON")
    private Map<String, Object> specification;

    @Column(name = "sales_count", nullable = false)
    private int salesCount;

    @Column(name = "scrap_count", nullable = false)
    private int scrapCount;

    // 연관관계 매핑
    @Builder.Default
    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariantEntity> variants = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImageEntity> images = new ArrayList<>();

    // Domain → Entity 변환
    public static ProductEntity from(Product product) {
        ProductEntity entity = ProductEntity.builder()
                .type(product.getType())
                .status(product.getStatus())
                .name(product.getName())
                .description(product.getDescription())
                .specification(product.getSpecification())
                .salesCount(product.getSalesCount())
                .scrapCount(product.getScrapCount())
                .build();

        // 품목들 매핑
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            for (ProductVariant variant : product.getVariants()) {
                ProductVariantEntity variantEntity = ProductVariantEntity.from(variant, entity);
                entity.getVariants().add(variantEntity);
            }
        }

        // 이미지들 매핑
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            for (ProductImage image : product.getImages()) {
                ProductImageEntity imageEntity = ProductImageEntity.from(image, entity);
                entity.getImages().add(imageEntity);
            }
        }

        return entity;
    }

    // Entity → Domain 변환
    public Product toDomain() {
        return Product.builder()
                .id(this.id)
                .type(this.type)
                .status(this.status)
                .name(this.name)
                .description(this.description)
                .specification(this.specification)
                .salesCount(this.salesCount)
                .scrapCount(this.scrapCount)
                .variants(this.variants != null
                        ? this.variants.stream().map(ProductVariantEntity::toDomain).collect(Collectors.toList())
                        : List.of())
                .images(this.images != null
                        ? this.images.stream().map(ProductImageEntity::toDomain).collect(Collectors.toList())
                        : List.of())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }

}