package com.kakaotech.ott.ott.orderItem.domain.repository;

import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;

import java.util.List;

public interface OrderItemRepository {

    OrderItem save(OrderItem orderItem);

    void existsByProductIdAndPendingProductStatus(Long productId, OrderItemStatus orderItemStatusPending, OrderItemStatus orderItemStatusCanceled);

    List<OrderItem> findByProductOrderId(Long productOrderId);

    void cancelOrderItem(List<OrderItem> orderItems);

    void refundOrderItem(List<OrderItem> orderItems);

    void confirmOrderItem(List<OrderItem> orderItems);
}
