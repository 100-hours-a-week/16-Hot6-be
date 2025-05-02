package com.kakaotech.ott.ott.aiImage.infrastructure.entity;

import com.kakaotech.ott.ott.aiImage.domain.model.ProductSubCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_sub_category")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ProductSubCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_category_id") // FK 컬럼명
    private ProductMainCategoryEntity productMainCategoryEntity;

    @Column(name = "name")
    private String name;

    public ProductSubCategory toDomain() {

        return ProductSubCategory.builder()
                .id(this.id)
                .mainCategoryId(this.productMainCategoryEntity.getId())
                .name(this.name)
                .build();
    }

    public static ProductSubCategoryEntity from(ProductSubCategory productSubCategory, ProductMainCategoryEntity productMainCategoryEntity) {

        return ProductSubCategoryEntity.builder()
                .productMainCategoryEntity(productMainCategoryEntity)
                .name(productSubCategory.getName())
                .build();
    }
}
