package com.kakaotech.ott.ott.product.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaotech.ott.ott.product.domain.model.ProductType;
import com.kakaotech.ott.ott.product.domain.model.PromotionStatus;
import com.kakaotech.ott.ott.product.domain.model.PromotionType;
import com.kakaotech.ott.ott.product.domain.model.VariantStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductGetResponseDto {
    @JsonProperty("product_type")
    private ProductType productType;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("specification")
    private Map<String, Object> specification;

    @JsonProperty("variants")
    private List<VariantResponse> variants;

    @JsonProperty("scraped")
    private Boolean scraped;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantResponse {

        @JsonProperty("status")
        private VariantStatus status;

        @JsonProperty("name")
        private String name;

        @JsonProperty("price")
        private int price;

        @JsonProperty("available_quantity")
        private int availableQuantity;

        @JsonProperty("reserved_quantity")
        private int reservedQuantity;

        @JsonProperty("image_urls")
        private List<String> imageUrls;

        @JsonProperty("promotions")
        private List<PromotionResponse> promotions;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PromotionResponse {

        @JsonProperty("status")
        private PromotionStatus status;

        @JsonProperty("type")
        private PromotionType type;

        @JsonProperty("name")
        private String name;

        @JsonProperty("discount_price")
        private int discountPrice;

        @JsonProperty("rate")
        private BigDecimal rate;

        @JsonProperty("promotion_quantity")
        private int promotionQuantity;

        @JsonProperty("start_at")
        private LocalDateTime startAt;

        @JsonProperty("end_at")
        private LocalDateTime endAt;

        @JsonProperty("max_per_customer")
        private int maxPerCustomer;
    }
}
