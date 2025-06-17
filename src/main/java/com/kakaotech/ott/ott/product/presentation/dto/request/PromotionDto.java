package com.kakaotech.ott.ott.product.presentation.dto.request;

import com.kakaotech.ott.ott.product.domain.model.ProductType;
import com.kakaotech.ott.ott.product.domain.model.PromotionType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDto {

    @NotNull(message = "프로모션 타입은 필수입니다")
    private PromotionType type;

    @NotBlank(message = "프로모션명은 필수입니다")
    @Size(max = 50, message = "프로모션명은 최대 50자까지 가능합니다")
    private String name;

    @NotNull(message = "할인가는 필수입니다")
    @Min(value = 0, message = "할인가는 0 이상이어야 합니다")
    private Integer discountPrice;

    @NotNull(message = "총 재고는 필수입니다")
    @Min(value = 0, message = "총 재고는 0 이상이어야 합니다")
    private Integer totalQuantity;

    @NotNull(message = "시작 시간은 필수입니다")
    private LocalDateTime startAt;

    @NotNull(message = "종료 시간은 필수입니다")
    private LocalDateTime endAt;

    @NotNull(message = "인당 구매 가능 개수는 필수입니다")
    @Min(value = 1, message = "인당 구매 가능 개수는 1 이상이어야 합니다")
    private Integer maxPerCustomer;
}