package com.kakaotech.ott.ott.productOrder.domain.repository;

import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.user.domain.model.User;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductOrderRepository {

    ProductOrder save(ProductOrder productOrder, User user);

    void deleteProductOrder(ProductOrder productOrder, User user);

    void confirmProductOrder(ProductOrder productOrder, User user);

    void confirmProductOrder(ProductOrder productOrder);

    Slice<ProductOrder> findAllByUserId(Long userId, Long lastOrderId, int size);

    ProductOrder findByIdAndUserId(Long orderId, Long userId);

    List<ProductOrder> findOrdersToAutoConfirm(LocalDateTime now);
}
