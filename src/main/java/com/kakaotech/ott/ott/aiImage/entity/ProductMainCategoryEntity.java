package com.kakaotech.ott.ott.aiImage.entity;

import com.kakaotech.ott.ott.aiImage.domain.ProductMainCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_main_category")
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
