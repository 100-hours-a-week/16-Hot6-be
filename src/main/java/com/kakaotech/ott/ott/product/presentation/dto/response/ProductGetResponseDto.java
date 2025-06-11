package com.kakaotech.ott.ott.product.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductGetResponseDto {
    @JsonProperty("product_type")
    private String productType;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("specification")
    private Map<String, String> specification;

    @JsonProperty("variants")
    private List<VariantResponse> variants;

    @JsonProperty("image_urls")
    private List<String> imageUrls;

    @JsonProperty("scraped")
    private Boolean scraped;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantResponse {

        @JsonProperty("status")
        private String status;

        @JsonProperty("name")
        private String name;

        @JsonProperty("price")
        private int price;

        @JsonProperty("available_quantity")
        private int availableQuantity;

        @JsonProperty("reserved_quantity")
        private int reservedQuantity;

        @JsonProperty("promotions")
        private List<PromotionResponse> promotions;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PromotionResponse {

        @JsonProperty("status")
        private String status;

        @JsonProperty("type")
        private String type;

        @JsonProperty("name")
        private String name;

        @JsonProperty("discount_price")
        private int discountPrice;

        @JsonProperty("rate")
        private int rate;

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
