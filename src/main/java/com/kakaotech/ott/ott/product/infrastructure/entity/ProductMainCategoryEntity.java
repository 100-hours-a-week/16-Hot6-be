package com.kakaotech.ott.ott.product.infrastructure.entity;

import com.kakaotech.ott.ott.product.domain.model.ProductMainCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_main_category")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ProductMainCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    public ProductMainCategory toDomain() {

        return ProductMainCategory.builder()
                .id(this.id)
                .name(this.name)
                .build();
    }

    public static ProductMainCategoryEntity from(ProductMainCategory productMainCategory) {

        return ProductMainCategoryEntity.builder()
                .name(productMainCategory.getName())
                .build();
    }

}
