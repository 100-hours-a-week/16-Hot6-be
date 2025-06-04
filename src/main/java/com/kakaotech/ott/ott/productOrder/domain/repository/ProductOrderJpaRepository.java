package com.kakaotech.ott.ott.productOrder.domain.repository;

import com.kakaotech.ott.ott.productOrder.infrastructure.entity.ProductOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOrderJpaRepository extends JpaRepository<ProductOrderEntity, Long> {
}
