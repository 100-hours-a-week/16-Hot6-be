package com.kakaotech.ott.ott.payment.domain.repository;

import com.kakaotech.ott.ott.payment.infrastructure.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByProductOrderEntity_Id(Long productOrderId);
}
