package com.kakaotech.ott.ott.productOrder.domain.repository;

import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.user.domain.model.User;
import org.springframework.data.domain.Slice;

public interface ProductOrderRepository {

    ProductOrder save(ProductOrder productOrder, User user);

    ProductOrder update(ProductOrder productOrder, User user);

    Slice<ProductOrder> findAllByUserId(Long userId, Long lastOrderId, int size);

    ProductOrder findByIdAndUserId(Long orderId, Long userId);
}
