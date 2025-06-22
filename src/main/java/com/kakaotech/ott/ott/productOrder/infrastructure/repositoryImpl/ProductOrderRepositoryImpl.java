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

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductOrderRepositoryImpl implements ProductOrderRepository {

    private final ProductOrderJpaRepository productOrderJpaRepository;

    @Override
    public ProductOrder save(ProductOrder productOrder, User user) {

        ProductOrderEntity productOrderEntity = ProductOrderEntity.from(productOrder, UserEntity.from(user));

        return productOrderJpaRepository.save(productOrderEntity).toDomain();
    }

    @Override
    public ProductOrder paymentOrder(ProductOrder productOrder) {

        ProductOrderEntity productOrderEntity = productOrderJpaRepository.findByIdAndUserEntity_IdAndDeletedAtIsNull(
                productOrder.getId(), productOrder.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        productOrderEntity.setStatus(productOrder.getStatus());

        return productOrderJpaRepository.save(productOrderEntity).toDomain();
    }

    @Override
    public void deleteProductOrder(ProductOrder productOrder, User user) {

        ProductOrderEntity beforeProductOrderEntity = productOrderJpaRepository.findByIdAndUserEntity_IdAndDeletedAtIsNullAndStatusNot(productOrder.getId(), user.getId(), ProductOrderStatus.PENDING)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        beforeProductOrderEntity.setStatus(productOrder.getStatus());
        beforeProductOrderEntity.setDeletedAt(productOrder.getDeletedAt());
    }

    @Override
    public void deleteProductOrder(ProductOrder productOrder) {

        ProductOrderEntity beforeProductOrderEntity = productOrderJpaRepository.findByIdAndDeletedAtIsNull(productOrder.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        beforeProductOrderEntity.setStatus(productOrder.getStatus());
        beforeProductOrderEntity.setDeletedAt(productOrder.getDeletedAt());
    }

    @Override
    public ProductOrder confirmProductOrder(ProductOrder productOrder, User user) {

        ProductOrderEntity beforeProductOrderEntity = productOrderJpaRepository.findByIdAndUserEntity_IdAndDeletedAtIsNullAndStatusNot(productOrder.getId(), user.getId(), ProductOrderStatus.PENDING)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        beforeProductOrderEntity.setConfirmedAt(productOrder.getConfirmedAt());
        beforeProductOrderEntity.setStatus(productOrder.getStatus());

        return productOrderJpaRepository.save(beforeProductOrderEntity).toDomain();
    }

    @Override
    public void confirmProductOrder(ProductOrder productOrder) {

        ProductOrderEntity beforeProductOrderEntity = productOrderJpaRepository.findByIdAndDeletedAtIsNull(productOrder.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        beforeProductOrderEntity.setConfirmedAt(productOrder.getConfirmedAt());
        beforeProductOrderEntity.setStatus(ProductOrderStatus.CONFIRMED);
    }

    @Override
    public void cancelProductOrder(ProductOrder productOrder, User user) {

        ProductOrderEntity beforeProductOrderEntity = productOrderJpaRepository.findByIdAndUserEntity_IdAndDeletedAtIsNullAndStatusNot(productOrder.getId(), user.getId(), ProductOrderStatus.PENDING)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        beforeProductOrderEntity.setCanceledAt(productOrder.getCanceledAt());
        beforeProductOrderEntity.setStatus(productOrder.getStatus());
    }

    @Override
    public void refundRequestProductOrder(ProductOrder productOrder, User user) {

        ProductOrderEntity beforeProductOrderEntity = productOrderJpaRepository.findByIdAndUserEntity_IdAndDeletedAtIsNullAndStatusNot(productOrder.getId(), user.getId(), ProductOrderStatus.PENDING)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        beforeProductOrderEntity.setStatus(productOrder.getStatus());
    }

    @Override
    public void refundProductOrder(ProductOrder productOrder) {

        ProductOrderEntity productOrderEntity = productOrderJpaRepository.findById(productOrder.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        productOrderEntity.setRefundedAt(productOrder.getRefundedAt());
        productOrderEntity.setStatus(productOrder.getStatus());
    }

    @Override
    public void deliveryProductOrder(ProductOrder productOrder) {
        ProductOrderEntity productOrderEntity = productOrderJpaRepository.findById(productOrder.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        productOrderEntity.setDeletedAt(productOrder.getDeliveredAt());
        productOrderEntity.setStatus(productOrder.getStatus());
    }

    @Override
    public Slice<ProductOrder> findAllByUserId(Long userId, Long lastOrderId, int size) {

        Slice<ProductOrderEntity> slice = productOrderJpaRepository.findUserAllProductOrders(userId, lastOrderId, PageRequest.of(0, size));

        return slice.map(ProductOrderEntity::toDomain);
    }

    @Override
    public ProductOrder findByIdAndUserId(Long orderId, Long userId) {

        ProductOrderEntity beforeProductOrderEntity = productOrderJpaRepository.findByIdAndUserEntity_IdAndDeletedAtIsNull(orderId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        return beforeProductOrderEntity.toDomain();
    }

    @Override
    public List<ProductOrder> findOrdersToAutoConfirm(LocalDateTime threshold) {

        return productOrderJpaRepository.findOrdersToAutoConfirm(threshold)
                .stream()
                .map(ProductOrderEntity::toDomain)
                .toList();
    }

    @Override
    public List<ProductOrder> findOrdersToAutoDelete(LocalDateTime threshold) {
        return productOrderJpaRepository.findOrdersToAutoDelete(threshold)
                .stream()
                .map(ProductOrderEntity::toDomain)
                .toList();
    }

    @Override
    public ProductOrder findByIdAndUserIdToPayment(Long orderId, Long userId) {

        return productOrderJpaRepository.findByIdAndUserEntity_IdAndDeletedAtIsNull(orderId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND))
                .toDomain();
    }

    @Override
    public boolean existsByUserIdAndFingerprint(Long userId, String fingerprint) {

        return productOrderJpaRepository.existsByUserEntityIdAndFingerprint(userId, fingerprint);
    }

    @Override
    public ProductOrder findById(Long orderId) {

        return productOrderJpaRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND))
                .toDomain();
    }
}
