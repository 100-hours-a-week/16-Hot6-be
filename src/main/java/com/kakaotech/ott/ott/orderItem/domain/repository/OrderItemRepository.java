package com.kakaotech.ott.ott.orderItem.domain.repository;

import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;

import java.util.List;

public interface OrderItemRepository {

    OrderItem save(OrderItem orderItem);

    void existsByProductIdAndStatus(Long productId, OrderItemStatus orderItemStatusCanceled);

    List<OrderItem> findByProductOrderId(Long productOrderId);

    void payOrderItem(List<OrderItem> orderItems);

    void cancelOrderItem(List<OrderItem> orderItems);

    void refundOrderItem(List<OrderItem> orderItems);

    void confirmOrderItem(List<OrderItem> orderItems);

    void deleteOrderItem(OrderItem orderItems);
}
