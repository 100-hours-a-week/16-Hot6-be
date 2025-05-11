package com.kakaotech.ott.ott.product.domain.model;

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

}
