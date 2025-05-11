package com.kakaotech.ott.ott.aiImage.presentation.dto.response;

import com.kakaotech.ott.ott.product.presentation.dto.response.ProductResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiImageAndProductResponseDto {

    private AiImageResponseDto image;

    private List<ProductResponseDto> products;

}
