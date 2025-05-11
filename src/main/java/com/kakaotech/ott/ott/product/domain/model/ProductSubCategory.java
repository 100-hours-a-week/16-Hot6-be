package com.kakaotech.ott.ott.product.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProductSubCategory {

    private Long id;

    private Long mainCategoryId;

    private String name;

    public static ProductSubCategory createProductSubCategory(Long mainCategoryId, String name) {

        return ProductSubCategory.builder()
                .mainCategoryId(mainCategoryId)
                .name(name)
                .build();
    }
}
