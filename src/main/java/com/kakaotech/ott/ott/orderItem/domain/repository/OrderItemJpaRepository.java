package com.kakaotech.ott.ott.orderItem.domain.repository;

import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;
import com.kakaotech.ott.ott.orderItem.infrastructure.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemEntity, Long> {
    boolean existsByProductIdAndPendingProductStatus(Long productId, OrderItemStatus status);
}
