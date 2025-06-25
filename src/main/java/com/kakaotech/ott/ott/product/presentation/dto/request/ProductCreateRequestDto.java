package com.kakaotech.ott.ott.product.presentation.dto.request;

import com.kakaotech.ott.ott.product.domain.model.ProductType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequestDto {

    @NotNull(message = "상품 정보는 필수입니다")
    @Valid
    private ProductInfo product;

    @NotNull(message = "품목 정보는 필수입니다")
    @Size(min = 1, message = "품목은 최소 1개 이상 필요합니다")
    @Valid
    private List<VariantDto> variants;

    private Map<Integer, List<MultipartFile>> variantImagesMap;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductInfo {
        @NotNull(message = "상품 타입은 필수입니다")
        private ProductType type;

        @NotBlank(message = "상품명은 필수입니다")
        @Size(max = 50, message = "상품명은 최대 50자까지 가능합니다")
        private String name;

        @NotBlank(message = "상품 설명은 필수입니다")
        @Size(max = 1000, message = "상품 설명은 최대 1000자까지 가능합니다")
        private String description;

        @NotNull(message = "사양 정보는 필수입니다")
        private Map<String, Object> specification;
    }
}