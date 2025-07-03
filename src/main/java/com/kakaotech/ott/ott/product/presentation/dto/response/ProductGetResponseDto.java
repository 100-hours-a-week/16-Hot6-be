package com.kakaotech.ott.ott.product.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaotech.ott.ott.product.domain.model.ProductType;
import com.kakaotech.ott.ott.product.domain.model.PromotionStatus;
import com.kakaotech.ott.ott.product.domain.model.PromotionType;
import com.kakaotech.ott.ott.product.domain.model.VariantStatus;
import com.kakaotech.ott.ott.util.KstDateTime;
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
    @JsonProperty("product_id")
    private Long productId;

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

//    @JsonProperty("scraped")
//    private Boolean scraped;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantResponse {
        @JsonProperty("variant_id")
        private Long variantId;

        @JsonProperty("status")
        private VariantStatus status;

        @JsonProperty("name")
        private String name;

        @JsonProperty("price")
        private int price;

        @JsonProperty("available_quantity")
        private int availableQuantity;

        @JsonProperty("image_urls")
        private List<String> imageUrls;

        @JsonProperty("promotions")
        private List<PromotionResponse> promotions;

        @JsonProperty("scraped")
        private Boolean scraped;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PromotionResponse {
        @JsonProperty("promotion_id")
        private Long promotionId;

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

        @JsonProperty("available_quantity")
        private int availableQuantity;

        @JsonProperty("start_at")
        private KstDateTime startAt;

        @JsonProperty("end_at")
        private KstDateTime endAt;

        @JsonProperty("max_per_customer")
        private int maxPerCustomer;
    }
}
