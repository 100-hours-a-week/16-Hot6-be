package com.kakaotech.ott.ott.product.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    // 버전 업데이트 후 진행
//    @NotNull(message = "center_x는 필수입니다.")
//    @JsonProperty("center_x")
//    private Integer centerX;
//
//    @NotNull(message = "center_y는 필수입니다.")
//    @JsonProperty("center_y")
//    private Integer centerY;

    @NotNull(message = "image_path는 필수입니다.")
    @JsonProperty("image_path")
    private String imagePath;

    @NotNull(message = "main_category는 필수입니다.")
    @JsonProperty("main_category")
    private String mainCategory;

    @NotNull(message = "sub_category는 필수입니다.")
    @JsonProperty("sub_category")
    private String subCategory;
}
