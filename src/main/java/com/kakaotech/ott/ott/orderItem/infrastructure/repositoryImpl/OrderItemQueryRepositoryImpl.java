package com.kakaotech.ott.ott.orderItem.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemQueryRepository;
import com.kakaotech.ott.ott.orderItem.infrastructure.entity.QOrderItemEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.QProductImageEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.QProductVariantEntity;
import com.kakaotech.ott.ott.productOrder.presentation.dto.response.ProductInfoDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderItemQueryRepositoryImpl implements OrderItemQueryRepository {

    private final JPAQueryFactory queryFactory;

    QOrderItemEntity orderItem = QOrderItemEntity.orderItemEntity;
    QProductVariantEntity productVariant = QProductVariantEntity.productVariantEntity;
    QProductImageEntity productImage = QProductImageEntity.productImageEntity;

    @Override
    public List<ProductInfoDto> findAllByOrderIds(List<Long> orderIds) {
        return queryFactory
                .select(Projections.constructor(ProductInfoDto.class,
                        orderItem.productOrderEntity.id,
                        productVariant.id,
                        productVariant.name,
                        productImage.imageUuid,
                        orderItem.quantity,
                        orderItem.finalPrice.divide(orderItem.quantity),
                        orderItem.status.stringValue()
                ))
                .from(orderItem)
                .join(productVariant).on(orderItem.productVariantEntity.id.eq(productVariant.id))
                .leftJoin(productImage).on(
                        productImage.variantEntity.id.eq(productVariant.id)
                                .and(productImage.sequence.eq(1))
                )
                .where(orderItem.productOrderEntity.id.in(orderIds))
                .fetch();
    }
}
