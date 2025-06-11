package com.kakaotech.ott.ott.productOrder.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderPartialCancelRequestDto {

    private List<Long> orderItemIds;
}
