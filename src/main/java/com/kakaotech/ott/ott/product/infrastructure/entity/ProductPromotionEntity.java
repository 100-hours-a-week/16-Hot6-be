package com.kakaotech.ott.ott.product.infrastructure.entity;

import com.kakaotech.ott.ott.product.domain.model.PromotionStatus;
import com.kakaotech.ott.ott.product.domain.model.PromotionType;
import com.kakaotech.ott.ott.product.domain.model.ProductPromotion;
import com.kakaotech.ott.ott.util.AuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "service_product_promotions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProductPromotionEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 품목과의 관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariantEntity variantEntity;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PromotionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private PromotionType type;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "original_price", nullable = false)
    private int originalPrice;

    @Column(name = "discount_price", nullable = false)
    private int discountPrice;

    @Column(name = "rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal rate;

    @Column(name = "promotion_quantity", nullable = false)
    private int promotionQuantity;

    @Column(name = "sold_quantity", nullable = false)
    private int soldQuantity;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "max_per_customer", nullable = false)
    private int maxPerCustomer;

    // Domain → Entity 변환
    public static ProductPromotionEntity from(ProductPromotion promotion, ProductVariantEntity variantEntity) {
        return ProductPromotionEntity.builder()
                .id(promotion.getId())
                .variantEntity(variantEntity)
                .status(promotion.getStatus())
                .type(promotion.getType())
                .name(promotion.getName())
                .originalPrice(promotion.getOriginalPrice())
                .discountPrice(promotion.getDiscountPrice())
                .rate(promotion.getRate())
                .promotionQuantity(promotion.getPromotionQuantity())
                .soldQuantity(promotion.getSoldQuantity())
                .startAt(promotion.getStartAt())
                .endAt(promotion.getEndAt())
                .maxPerCustomer(promotion.getMaxPerCustomer())
                .build();
    }

    // Entity → Domain 변환
    public ProductPromotion toDomain() {
        return ProductPromotion.builder()
                .id(this.id)
                .variantId(this.variantEntity.getId())
                .status(this.status)
                .type(this.type)
                .name(this.name)
                .originalPrice(this.originalPrice)
                .discountPrice(this.discountPrice)
                .rate(this.rate)
                .promotionQuantity(this.promotionQuantity)
                .soldQuantity(this.soldQuantity)
                .startAt(this.startAt)
                .endAt(this.endAt)
                .maxPerCustomer(this.maxPerCustomer)
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }

}