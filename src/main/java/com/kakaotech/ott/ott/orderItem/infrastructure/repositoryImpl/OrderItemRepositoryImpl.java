package com.kakaotech.ott.ott.orderItem.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;
import com.kakaotech.ott.ott.product.domain.repository.ProductPromotionJpaRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductVariantJpaRepository;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductPromotionEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductVariantEntity;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderJpaRepository;
import com.kakaotech.ott.ott.productOrder.infrastructure.entity.ProductOrderEntity;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemJpaRepository;
import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemRepository;
import com.kakaotech.ott.ott.orderItem.infrastructure.entity.OrderItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemJpaRepository orderItemJpaRepository;
    private final ProductOrderJpaRepository productOrderJpaRepository;
    private final ProductVariantJpaRepository productVariantJpaRepository;
    private final ProductPromotionJpaRepository productPromotionJpaRepository;

    @Override
    public OrderItem save(OrderItem orderItem) {

        ProductOrderEntity productOrderEntity = productOrderJpaRepository.findById(orderItem.getOrderId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        ProductVariantEntity productVariantEntity = productVariantJpaRepository.findById(orderItem.getVariantsId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        OrderItemEntity orderItemEntity;

        if(orderItem.getPromotionId() != null) {
            ProductPromotionEntity productPromotionEntity = productPromotionJpaRepository.findById(orderItem.getPromotionId())
                    .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));    // TODO: 에러 코드 변경해야됨

            orderItemEntity = OrderItemEntity.from(orderItem, productOrderEntity, productVariantEntity, productPromotionEntity);
        } else {
            orderItemEntity = OrderItemEntity.from(orderItem, productOrderEntity, productVariantEntity, null);
        }

        return orderItemJpaRepository.save(orderItemEntity).toDomain();
    }

    @Override
    public void existsByProductIdAndPendingProductStatus(Long productId, OrderItemStatus orderItemStatusPending, OrderItemStatus orderItemStatusCanceled) {

        boolean exists = orderItemJpaRepository.existsByProductVariantEntityIdAndPendingProductStatusAndStatus(productId, orderItemStatusPending, orderItemStatusCanceled);

        if(exists) {
            throw new CustomException(ErrorCode.ALREADY_ORDERED);
        }
    }

    @Override
    public List<OrderItem> findByProductOrderId(Long productOrderId) {

        List<OrderItemEntity> entities = orderItemJpaRepository.findByProductOrderEntity_Id(productOrderId);
        return entities.stream()
                .map(OrderItemEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelOrderItem(List<OrderItem> orderItems) {

        List<OrderItemEntity> entities = orderItemJpaRepository.findByProductOrderEntity_Id(orderItems.getFirst().getOrderId());

        Map<Long, OrderItemEntity> entityMap = entities.stream()
                .collect(Collectors.toMap(OrderItemEntity::getId, Function.identity()));

        for(OrderItem item : orderItems) {

            if(item.getStatus().equals(OrderItemStatus.CANCELED)) {
                OrderItemEntity entity = entityMap.get(item.getId());
                if (entity == null)
                    throw new CustomException(ErrorCode.ORDER_ITEM_NOT_FOUND);

                entity.cancel(item);
            }
        }

    }

    @Override
    public void refundOrderItem(List<OrderItem> orderItems) {

        List<OrderItemEntity> entities = orderItemJpaRepository.findByProductOrderEntity_Id(orderItems.getFirst().getOrderId());

        Map<Long, OrderItemEntity> entityMap = entities.stream()
                .collect(Collectors.toMap(OrderItemEntity::getId, Function.identity()));

        for(OrderItem item : orderItems) {
            OrderItemEntity entity = entityMap.get(item.getId());
            if(entity == null)
                throw new CustomException(ErrorCode.ORDER_ITEM_NOT_FOUND);

            entity.refund(item);
        }
    }

    @Override
    public void confirmOrderItem(List<OrderItem> orderItems) {
        List<OrderItemEntity> entities = orderItemJpaRepository.findByProductOrderEntity_Id(orderItems.getFirst().getOrderId());

        Map<Long, OrderItemEntity> entityMap = entities.stream()
                .collect(Collectors.toMap(OrderItemEntity::getId, Function.identity()));

        for(OrderItem item : orderItems) {
            OrderItemEntity entity = entityMap.get(item.getId());
            if (entity == null)
                throw new CustomException(ErrorCode.ORDER_ITEM_NOT_FOUND);

            entity.confirm(item);
        }
    }
}
