package com.kakaotech.ott.ott.orderItem.domain.repository;

import com.kakaotech.ott.ott.productOrder.presentation.dto.response.ProductInfoDto;

import java.util.List;

public interface OrderItemQueryRepository {

    List<ProductInfoDto> findAllByOrderIds(List<Long> orderIds);
}
