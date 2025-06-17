package com.kakaotech.ott.ott.recommendProduct.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeskProductListResponseDto {

    private List<DeskProducts> deskProducts;
    private Pagination pagination;

    @Getter
    @AllArgsConstructor
    public static class DeskProducts {
        private Long productId;

        private String productName;

        private String subCategory;

        private int price;

        private boolean isScraped;

        private double weight;

        private String imageUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class Pagination {
        private int size;
        private Double lastWeight;
        private Long lastDeskProductId;
        private boolean hasNext;
    }
}
