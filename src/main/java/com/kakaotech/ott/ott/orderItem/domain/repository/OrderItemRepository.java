package com.kakaotech.ott.ott.orderItem.domain.repository;

import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;

public interface OrderItemRepository {

    OrderItem save(OrderItem orderItem);

    void existsByProductIdAndPendingProductStatus(Long productId, OrderItemStatus orderItemStatus);
}
