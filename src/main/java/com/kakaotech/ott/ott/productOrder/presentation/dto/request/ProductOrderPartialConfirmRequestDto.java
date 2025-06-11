package com.kakaotech.ott.ott.productOrder.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderPartialConfirmRequestDto {

    private Long orderItemId;
}
