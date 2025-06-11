package com.kakaotech.ott.ott.recommendProduct.infrastructure.entity;

import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.recommendProduct.domain.model.DeskProduct;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "desk_products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class DeskProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id") // FK 컬럼명
    private ProductSubCategoryEntity productSubCategoryEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_image_id") // FK 컬럼명
    private AiImageEntity aiImageEntity;

    @Column(name = "name", nullable = false, length = 255)
    private String name;
    @Column(name = "price", nullable = false)   // 비즈니스 로직에서 unsigned 확인 처리
    private Integer price;
    @Column(name = "purchase_place", nullable = false, length = 255)
    private String purchasePlace;

    @Column(name = "scrap_count", nullable = false)
    private Integer scrapCount;
    @Column(name = "click_count", nullable = false)
    private Integer clickCount;
    @Column(name = "weight", nullable = false)
    private Integer weight;

    @Column(name = "purchase_url", nullable = false, length = 255)
    private String purchaseUrl;

    @Column(name = "center_x", nullable = false)
    private Integer centerX;
    @Column(name = "center_y", nullable = false)
    private Integer centerY;

    @Column(name = "image_path", nullable = false, length = 255)
    private String imagePath;

    public DeskProduct toDomain() {

        return DeskProduct.builder()
                .id(this.id)
                .subCategoryId(this.productSubCategoryEntity.getId())
                .aiImageId(this.aiImageEntity.getId())
                .name(this.name)
                .price(this.price)
                .purchasePlace(this.purchasePlace)
                .scrapCount(this.scrapCount)
                .clickCount(this.clickCount)
                .weight(this.weight)
                .purchaseUrl(this.purchaseUrl)
                .centerX(this.centerX)
                .centerY(this.centerY)
                .imagePath(this.imagePath)
                .build();

    }

    public static DeskProductEntity from(DeskProduct deskProduct, ProductSubCategoryEntity productSubCategoryEntity, AiImageEntity aiImageEntity) {

        return DeskProductEntity.builder()
                .productSubCategoryEntity(productSubCategoryEntity)
                .aiImageEntity(aiImageEntity)
                .name(deskProduct.getName())
                .price(deskProduct.getPrice())
                .purchasePlace(deskProduct.getPurchasePlace())
                .scrapCount(deskProduct.getScrapCount())
                .clickCount(deskProduct.getClickCount())
                .weight(deskProduct.getWeight())
                .purchaseUrl(deskProduct.getPurchaseUrl())
                .centerX(deskProduct.getCenterX())
                .centerY(deskProduct.getCenterY())
                .imagePath(deskProduct.getImagePath())
                .build();
    }

}
