package com.kakaotech.ott.ott.product.infrastructure.entity;

import com.kakaotech.ott.ott.product.domain.model.ProductPromotion;
import com.kakaotech.ott.ott.product.domain.model.ProductVariant;
import com.kakaotech.ott.ott.product.domain.model.VariantStatus;
import com.kakaotech.ott.ott.util.AuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "service_product_variants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProductVariantEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상품과의 관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity productEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private VariantStatus status;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;

    @Column(name = "is_on_promotion", nullable = false)
    private boolean isOnPromotion;

    // 특가와의 관계 (1:N)
    @Builder.Default
    @OneToMany(mappedBy = "variantEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductPromotionEntity> promotions = new ArrayList<>();

    // Domain → Entity 변환
    public static ProductVariantEntity from(ProductVariant variant, ProductEntity productEntity) {
        ProductVariantEntity entity = ProductVariantEntity.builder()
                .id(variant.getId())
                .productEntity(productEntity)
                .status(variant.getStatus())
                .name(variant.getName())
                .price(variant.getPrice())
                .availableQuantity(variant.getAvailableQuantity())
                .reservedQuantity(variant.getReservedQuantity())
                .isOnPromotion(variant.isOnPromotion())
                .build();

        // 특가들 매핑
        if (variant.getPromotions() != null && !variant.getPromotions().isEmpty()) {
            for (ProductPromotion promotion : variant.getPromotions()) {
                ProductPromotionEntity promotionEntity = ProductPromotionEntity.from(promotion, entity);
                entity.getPromotions().add(promotionEntity);
            }
        }

        return entity;
    }

    // Entity → Domain 변환
    public ProductVariant toDomain() {
        return ProductVariant.builder()
                .id(this.id)
                .productId(this.productEntity.getId())
                .status(this.status)
                .name(this.name)
                .price(this.price)
                .availableQuantity(this.availableQuantity)
                .reservedQuantity(this.reservedQuantity)
                .isOnPromotion(this.isOnPromotion)
                .promotions(this.promotions != null
                        ? this.promotions.stream().map(ProductPromotionEntity::toDomain).collect(Collectors.toList())
                        : List.of())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }

}