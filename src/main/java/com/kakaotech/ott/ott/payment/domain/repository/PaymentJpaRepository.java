package com.kakaotech.ott.ott.payment.domain.repository;

import com.kakaotech.ott.ott.payment.infrastructure.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {

}
