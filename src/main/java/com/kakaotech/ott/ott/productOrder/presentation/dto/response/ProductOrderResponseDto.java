package com.kakaotech.ott.ott.productOrder.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrderStatus;
import com.kakaotech.ott.ott.util.KstDateTime;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductOrderResponseDto {

    @NotNull
    private Long orderId;

    @NotNull
    private List<ServiceProductDto> products;

    @NotNull
    private int totalAmount;

    @NotNull
    private ProductOrderStatus status;

    @NotNull
    @JsonProperty("createdAt")
    private KstDateTime createdAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceProductDto {

        @NotNull
        private Long productId;

        private Long promotionId;

        @NotNull
        private int originalPrice;

        @NotNull
        private int quantity;

        @NotNull
        private int discountPrice;

    }
}
