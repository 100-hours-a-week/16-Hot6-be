package com.kakaotech.ott.ott.product.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantDto {

    @NotBlank(message = "품목명은 필수입니다")
    @Size(max = 50, message = "품목명은 최대 50자까지 가능합니다")
    private String name;

    @NotNull(message = "가격은 필수입니다")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다")
    private Integer price;

    @NotNull(message = "판매 가능 수량은 필수입니다")
    @Min(value = 0, message = "판매 가능 수량은 0 이상이어야 합니다")
    private Integer availableQuantity;

    @Valid
    private List<PromotionDto> promotions;
}