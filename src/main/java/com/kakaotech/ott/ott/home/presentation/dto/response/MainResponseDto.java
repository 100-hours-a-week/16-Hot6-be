package com.kakaotech.ott.ott.home.presentation.dto.response;

import com.kakaotech.ott.ott.post.presentation.dto.response.PopularSetupDto;
import com.kakaotech.ott.ott.product.presentation.dto.response.PromotionProductsDto;
import com.kakaotech.ott.ott.recommendProduct.presentation.dto.response.RecommendedItemsDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MainResponseDto {

    private List<PopularSetupDto> popularSetups;

    private List<RecommendedItemsDto> recommendedItems;

    private List<PromotionProductsDto> promotionProducts;
}
