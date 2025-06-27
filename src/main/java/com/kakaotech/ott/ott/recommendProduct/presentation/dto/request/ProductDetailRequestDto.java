package com.kakaotech.ott.ott.recommendProduct.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailRequestDto {

    @NotNull(message = "name은 필수입니다.")
    private String name;

    @NotNull(message = "price는 필수입니다.")
    private int price;

    @NotNull(message = "purchase_place는 필수입니다.")
    @JsonProperty("purchase_place")
    private String purchasePlace;

    @NotNull(message = "purchase_url은 필수입니다.")
    @JsonProperty("purchase_url")
    private String purchaseUrl;

    @NotNull(message = "image_path는 필수입니다.")
    @JsonProperty("image_path")
    private String imagePath;

    @NotNull(message = "main_category는 필수입니다.")
    @JsonProperty("main_category")
    private String mainCategory;

    @NotNull(message = "sub_category는 필수입니다.")
    @JsonProperty("sub_category")
    private String subCategory;

    // 버전 업데이트 후 진행
    @JsonProperty("center_x")
    private Integer centerX;

    @JsonProperty("center_y")
    private Integer centerY;

    @NotNull(message = "product_code는 필수입니다.")
    @JsonProperty("product_code")
    private String productCode;
}
