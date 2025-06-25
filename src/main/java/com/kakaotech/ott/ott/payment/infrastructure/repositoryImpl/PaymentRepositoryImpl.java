package com.kakaotech.ott.ott.payment.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.payment.domain.model.Payment;
import com.kakaotech.ott.ott.payment.domain.repository.PaymentJpaRepository;
import com.kakaotech.ott.ott.payment.domain.repository.PaymentRepository;
import com.kakaotech.ott.ott.payment.infrastructure.entity.PaymentEntity;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrderStatus;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderJpaRepository;
import com.kakaotech.ott.ott.productOrder.infrastructure.entity.ProductOrderEntity;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserJpaRepository;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final ProductOrderJpaRepository productOrderJpaRepository;

    @Override
    public Payment save(Payment payment, User user) {

        UserEntity userEntity = userJpaRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ProductOrderEntity productOrderEntity = productOrderJpaRepository.findByIdAndUserEntity_IdAndDeletedAtIsNull(
                payment.getOrderId(), user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        PaymentEntity paymentEntity = PaymentEntity.from(payment, productOrderEntity);

        return paymentJpaRepository.save(paymentEntity).toDomain();
    }

    @Override
    public void refund(Payment payment) {
        PaymentEntity paymentEntity = paymentJpaRepository.findById(payment.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        paymentEntity.refund(payment);
    }

    @Override
    public Payment findByProductOrderId(Long productOrderId) {

        return paymentJpaRepository.findByProductOrderEntity_Id(productOrderId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND))
                .toDomain();
    }
}
