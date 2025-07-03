package com.kakaotech.ott.ott.product.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaotech.ott.ott.util.KstDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ProductListResponseDto {
    private final List<Products> products;
    private final Pagination pagination;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class Products {
        @JsonProperty("product_id")
        private final Long productId;

        @JsonProperty("product_name")
        private final String productName;

        @JsonProperty("product_type")
        private final String productType;

        @JsonProperty("variant_id")
        private final Long variantId;

        @JsonProperty("variant_name")
        private final String variantName;

        @JsonProperty("image_url")
        private final String imageUrl;

        @JsonProperty("original_price")
        private final Integer originalPrice;

        @JsonProperty("discount_price")
        private final Integer discountPrice;

        @JsonProperty("discount_rate")
        private final BigDecimal discountRate;

        @JsonProperty("available_quantity")
        private final Integer availableQuantity;

        @JsonProperty("promotion_start_at")
        private final KstDateTime promotionStartAt;

        @JsonProperty("promotion_end_at")
        private final KstDateTime promotionEndAt;

        @JsonProperty("is_promotion")
        private final boolean promotion;

        @JsonProperty("scraped")
        private boolean scraped;

        @JsonProperty("created_at")
        private KstDateTime createdAt;

        public Products(Long productId, String productName, String productType,
                        Long variantId, String variantName, String imageUrl,
                        Integer originalPrice, Integer discountPrice, BigDecimal discountRate,
                        LocalDateTime promotionStartAt, LocalDateTime promotionEndAt, Integer availableQuantity,
                        boolean promotion, boolean scraped, LocalDateTime createdAt) {
            this.productId = productId;
            this.productName = productName;
            this.productType = productType;
            this.variantId = variantId;
            this.variantName = variantName;
            this.imageUrl = imageUrl;
            this.originalPrice = originalPrice;
            this.discountPrice = discountPrice;
            this.discountRate = discountRate;
            this.promotionStartAt = promotionStartAt != null ? new KstDateTime(promotionStartAt) : null;
            this.promotionEndAt = promotionEndAt != null ? new KstDateTime(promotionEndAt) : null;
            this.availableQuantity = availableQuantity;
            this.promotion = promotion;
            this.scraped = scraped;
            this.createdAt = new KstDateTime(createdAt);
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Pagination {
        private final int size;

        @JsonProperty("last_product_id")
        private final Long lastVariantId;
        @JsonProperty("has_next")
        private final boolean hasNext;
    }

}
