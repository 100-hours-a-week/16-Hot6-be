package com.kakaotech.ott.ott.admin.presentation.dto.request;

import com.kakaotech.ott.ott.product.presentation.dto.request.PromotionDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionCreateRequestDto {
    @Valid
    @NotNull(message = "프로모션 정보는 필수입니다.")
    private PromotionDto promotion;
}
