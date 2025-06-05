package com.kakaotech.ott.ott.productOrder.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderJpaRepository;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderRepository;
import com.kakaotech.ott.ott.productOrder.infrastructure.entity.ProductOrderEntity;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ProductOrderRepositoryImpl implements ProductOrderRepository {

    private final ProductOrderJpaRepository productOrderJpaRepository;

    @Override
    @Transactional
    public ProductOrder save(ProductOrder productOrder, User user) {

        ProductOrderEntity productOrderEntity = ProductOrderEntity.from(productOrder, UserEntity.from(user));

        return productOrderJpaRepository.save(productOrderEntity).toDomain();
    }

    @Override
    public Slice<ProductOrder> findAllByUserId(Long userId, Long lastOrderId, int size) {

        Slice<ProductOrderEntity> slice = productOrderJpaRepository.findUserAllProductOrders(userId, lastOrderId, PageRequest.of(0, size));

        return slice.map(ProductOrderEntity::toDomain);
    }

    @Override
    public ProductOrder findByIdAndUserId(Long orderId, Long userId) {

        ProductOrderEntity productOrderEntity = productOrderJpaRepository.findByIdAndUserEntity_IdAndDeletedAtIsNull(orderId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        return productOrderEntity.toDomain();
    }
}
