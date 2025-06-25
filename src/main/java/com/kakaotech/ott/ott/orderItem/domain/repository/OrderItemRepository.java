package com.kakaotech.ott.ott.orderItem.domain.repository;

import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;

import java.util.List;

public interface OrderItemRepository {

    OrderItem save(OrderItem orderItem);

    List<OrderItem> findByProductOrderId(Long productOrderId);

    void payOrderItem(List<OrderItem> orderItems);

    void cancelOrderItem(List<OrderItem> orderItems);

    void refundRequestOrderItem(List<OrderItem> orderItems);

    void confirmOrderItem(List<OrderItem> orderItems);

    void deleteOrderItem(OrderItem orderItems);

    List<OrderItem> findByStatus(OrderItemStatus status);

    OrderItem findById(Long orderItemId);

    void deliveryOrderItem(OrderItem orderItem);

    void refundOrderItem(OrderItem orderItem);

    int countByProductOrderIdAndStatusNot(Long productOrderId, OrderItemStatus status);
}
