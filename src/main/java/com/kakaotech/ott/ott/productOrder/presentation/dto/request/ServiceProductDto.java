package com.kakaotech.ott.ott.productOrder.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProductDto {

    @NotNull
    private Long productId;

    @NotNull
    private int price;

    @NotNull
    private int quantity;

}
