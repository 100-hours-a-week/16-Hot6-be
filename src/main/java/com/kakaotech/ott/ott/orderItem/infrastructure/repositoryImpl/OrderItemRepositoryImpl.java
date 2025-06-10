package com.kakaotech.ott.ott.orderItem.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderJpaRepository;
import com.kakaotech.ott.ott.productOrder.infrastructure.entity.ProductOrderEntity;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemJpaRepository;
import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemRepository;
import com.kakaotech.ott.ott.orderItem.infrastructure.entity.OrderItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemJpaRepository orderItemJpaRepository;
    private final ProductOrderJpaRepository productOrderJpaRepository;

    @Override
    @Transactional
    public OrderItem save(OrderItem orderItem) {

        ProductOrderEntity productOrderEntity = productOrderJpaRepository.findById(orderItem.getOrderId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        OrderItemEntity orderItemEntity = OrderItemEntity.from(orderItem, productOrderEntity);
        return orderItemJpaRepository.save(orderItemEntity).toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public void existsByProductIdAndPendingProductStatus(Long productId, OrderItemStatus orderItemStatus) {

        boolean exists = orderItemJpaRepository.existsByProductIdAndPendingProductStatus(productId, orderItemStatus);

        if(exists) {
            throw new CustomException(ErrorCode.ALREADY_ORDERED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItem> findByProductOrderId(Long productOrderId) {

        List<OrderItemEntity> entities = orderItemJpaRepository.findByProductOrderEntity_Id(productOrderId);
        return entities.stream()
                .map(OrderItemEntity::toDomain)
                .collect(Collectors.toList());
    }
}
