package com.kakaotech.ott.ott.productOrder.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderRequestDto {

    @NotNull
    private List<ServiceProductDto> products;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceProductDto {

        @NotNull
        private Long variantId;

        @NotNull
        private int quantity;
    }
}
