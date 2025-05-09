package com.kakaotech.ott.ott.aiImage.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AiImageAndProductRequestDto {

    @NotNull(message = "initial_image_url은 필수입니다.")
    @JsonProperty("initial_image_url")
    private String initialImageUrl;

    @NotNull(message = "processed_image_url은 필수입니다.")
    @JsonProperty("processed_image_url")
    private String processedImageUrl;

    @NotNull(message = "products는 필수입니다.")
    private List<ProductDetailRequestDto> products;
}
//{
//        "initial_image_url": "https://bucket.s3.amazonaws.com/images/test.png",
//        "processed_image_url": "https://bucket.s3.amazonaws.com/images/test.png",
//        "products": [
//        {
//        "name": "test",
//        "price": 30000,
//        "purchase_place": "네이버",
//        "purchase_url": "https://search.shopping.naver.com/...",
//        "image_path": "https://.../.jpg",
//        "main_category": "마우스",
//        "sub_category": "유선마우스"
//        },
//        {
//        "name": "test",
//        "price": 20000,
//        "purchase_place": "DeskProduct - purchase_place",
//        "purchase_url": "DeskProduct - purchase_url",
//        "image_path": "DeskProduct - iamge_url",
//        "main_category": "ProductMainCategory - name",
//        "sub_category": "ProductSubCategory - name"
//        }
//        ]
//        }
