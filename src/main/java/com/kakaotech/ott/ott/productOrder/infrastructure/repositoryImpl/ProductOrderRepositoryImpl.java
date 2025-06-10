package com.kakaotech.ott.ott.productOrder.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrderStatus;
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

import java.time.LocalDateTime;
import java.util.List;

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
    @Transactional
    public void deleteProductOrder(ProductOrder productOrder, User user) {

        ProductOrderEntity beforeProductOrderEntity = productOrderJpaRepository.findByIdAndUserEntity_IdAndDeletedAtIsNull(productOrder.getId(), user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        beforeProductOrderEntity.setStatus(productOrder.getStatus());
        beforeProductOrderEntity.setDeletedAt(productOrder.getDeletedAt());
    }

    @Override
    @Transactional
    public ProductOrder confirmProductOrder(ProductOrder productOrder, User user) {

        ProductOrderEntity beforeProductOrderEntity = productOrderJpaRepository.findByIdAndUserEntity_IdAndDeletedAtIsNull(productOrder.getId(), user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        beforeProductOrderEntity.setConfirmedAt(productOrder.getConfirmedAt());
        beforeProductOrderEntity.setStatus(productOrder.getStatus());

        return productOrderJpaRepository.save(beforeProductOrderEntity).toDomain();
    }

    @Override
    @Transactional
    public void confirmProductOrder(ProductOrder productOrder) {

        ProductOrderEntity beforeProductOrderEntity = productOrderJpaRepository.findByIdAndDeletedAtIsNull(productOrder.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        beforeProductOrderEntity.setConfirmedAt(productOrder.getConfirmedAt());
        beforeProductOrderEntity.setStatus(ProductOrderStatus.CONFIRMED);
    }

    @Override
    @Transactional
    public void cancelProductOrder(ProductOrder productOrder, User user) {

        ProductOrderEntity beforeProductOrderEntity = productOrderJpaRepository.findByIdAndUserEntity_IdAndDeletedAtIsNull(
                productOrder.getId(), user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        beforeProductOrderEntity.setCanceledAt(productOrder.getCanceledAt());
        beforeProductOrderEntity.setStatus(productOrder.getStatus());
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<ProductOrder> findAllByUserId(Long userId, Long lastOrderId, int size) {

        Slice<ProductOrderEntity> slice = productOrderJpaRepository.findUserAllProductOrders(userId, lastOrderId, PageRequest.of(0, size));

        return slice.map(ProductOrderEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductOrder findByIdAndUserId(Long orderId, Long userId) {

        ProductOrderEntity productOrderEntity = productOrderJpaRepository.findByIdAndUserEntity_IdAndDeletedAtIsNull(orderId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        return productOrderEntity.toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductOrder> findOrdersToAutoConfirm(LocalDateTime now) {

        return productOrderJpaRepository.findOrdersToAutoConfirm(now)
                .stream()
                .map(ProductOrderEntity::toDomain)
                .toList();
    }
}
