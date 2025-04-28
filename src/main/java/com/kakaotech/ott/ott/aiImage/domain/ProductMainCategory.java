package com.kakaotech.ott.ott.aiImage.domain;

import com.kakaotech.ott.ott.aiImage.entity.ProductMainCategoryEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductMainCategory {

    private Long id;

    private String name;

    @Builder
    public ProductMainCategory(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static ProductMainCategory createProductMainCategory(String name) {
        return ProductMainCategory.builder()
                .name(name)
                .build();
    }

    public ProductMainCategoryEntity toEntity() {

        return ProductMainCategoryEntity.builder()
                .name(this.name)
                .build();
    }

}
