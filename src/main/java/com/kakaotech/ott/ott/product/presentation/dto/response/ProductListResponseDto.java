package com.kakaotech.ott.ott.product.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ProductListResponseDto {
    private final List<Products> products;
    private final Pagination pagination;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Products {
        private final Long productId;
        private final String productName;
        private final String productType;
        private final String variantName;
        private final String imageUrl;
        private final Integer originalPrice;
        private final Integer discountPrice;
        private final BigDecimal discountRate;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private final LocalDateTime promotionEndAt;

        private final boolean isPromotion;
        private final boolean scrapped;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private final LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Pagination {
        private final int size;
        private final Long lastProductId;
        private final boolean hasNext;
    }

}
