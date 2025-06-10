package com.kakaotech.ott.ott.orderItem.domain.repository;

import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;

import java.util.List;

public interface OrderItemRepository {

    OrderItem save(OrderItem orderItem);

    void existsByProductIdAndPendingProductStatus(Long productId, OrderItemStatus orderItemStatus);

    List<OrderItem> findByProductOrderId(Long productOrderId);
}
