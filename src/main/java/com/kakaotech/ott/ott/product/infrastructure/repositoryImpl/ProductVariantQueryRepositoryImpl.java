package com.kakaotech.ott.ott.product.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.product.domain.model.ProductType;
import com.kakaotech.ott.ott.product.domain.model.PromotionStatus;
import com.kakaotech.ott.ott.product.domain.model.PromotionType;
import com.kakaotech.ott.ott.product.domain.repository.ProductVariantQueryRepository;
import com.kakaotech.ott.ott.product.infrastructure.entity.QProductEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.QProductImageEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.QProductPromotionEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.QProductVariantEntity;
import com.kakaotech.ott.ott.product.presentation.dto.response.ProductListResponseDto;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductVariantQueryRepositoryImpl implements ProductVariantQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final QProductVariantEntity   variant   = QProductVariantEntity.productVariantEntity;
    private final QProductEntity          product   = QProductEntity.productEntity;
    private final QProductImageEntity     image     = QProductImageEntity.productImageEntity;
    private final QProductPromotionEntity promotion = QProductPromotionEntity.productPromotionEntity;

    @Override
    public ProductListResponseDto findProductListByCursor(
            Long userId,
            ProductType productType,
            PromotionType promotionType,
            Long lastVariantId,
            int size
    ) {
        List<ProductListResponseDto.Products> result;

        if (promotionType == null) {
            // ────────── 일반 상품만 조회 (프로모션 컬럼은 모두 null) ──────────
            result = queryFactory
                    .select(productListProjectionWithoutPromotion())
                    .distinct()
                    .from(variant)
                    .join(variant.productEntity, product)
                    .leftJoin(variant.images, image)
                    .on(image.sequence.eq(1))
                    .where(
                            productTypeEq(productType),
                            variant.isOnPromotion.eq(false),
                            lastVariantIdLt(lastVariantId)
                    )
                    .orderBy(variant.id.desc())
                    .limit(size)
                    .fetch();
        } else {
            // ────────── 프로모션 상품만 조회 ──────────
            result = queryFactory
                    .select(productListProjectionWithPromotion())
                    .distinct()
                    .from(variant)
                    .join(variant.productEntity, product)
                    .leftJoin(variant.images, image)
                    .on(image.sequence.eq(1))
                    .join(variant.promotions, promotion)
                    .on(
                            promotion.status.in(PromotionStatus.ACTIVE, PromotionStatus.SOLD_OUT),
                            promotion.endAt.after(LocalDateTime.now())
                    )
                    .where(
                            productTypeEq(productType),
                            variant.isOnPromotion.eq(true),
                            lastVariantIdLt(lastVariantId)
                    )
                    .orderBy(promotion.endAt.asc().nullsLast())
                    .limit(size)
                    .fetch();
        }

        boolean hasNext = result.size() == size;
        Long nextLastVariantId = hasNext
                ? result.get(result.size() - 1).getVariantId()
                : null;

        ProductListResponseDto.Pagination pagination = ProductListResponseDto.Pagination.builder()
                .size(size)
                .lastVariantId(nextLastVariantId)
                .hasNext(hasNext)
                .build();

        return ProductListResponseDto.builder()
                .products(result)
                .pagination(pagination)
                .build();
    }

    /** 프로모션 정보 없이 조회할 때 (discountPrice, rate, startAt, endAt 모두 null) */
    private ConstructorExpression<ProductListResponseDto.Products> productListProjectionWithoutPromotion() {
        return Projections.constructor(
                ProductListResponseDto.Products.class,
                product.id,
                product.name,
                product.type.stringValue(),
                variant.id,
                variant.name,
                image.imageUuid,
                variant.price,
                Expressions.nullExpression(Integer.class),       // discountPrice 타입에 맞춰 변경
                Expressions.nullExpression(BigDecimal.class),        // rate
                Expressions.nullExpression(LocalDateTime.class), // startAt
                Expressions.nullExpression(LocalDateTime.class), // endAt
                variant.totalQuantity.subtract(variant.reservedQuantity).subtract(variant.soldQuantity),
                variant.isOnPromotion,
                Expressions.constant(false),
                product.createdAt
        );
    }

    /** 프로모션 정보 함께 조회할 때 */
    private ConstructorExpression<ProductListResponseDto.Products> productListProjectionWithPromotion() {
        return Projections.constructor(
                ProductListResponseDto.Products.class,
                product.id,
                product.name,
                product.type.stringValue(),
                variant.id,
                variant.name,
                image.imageUuid,
                variant.price,
                promotion.discountPrice,
                promotion.rate,
                promotion.startAt,
                promotion.endAt,
                variant.totalQuantity.subtract(variant.reservedQuantity).subtract(variant.soldQuantity),
                variant.isOnPromotion,
                Expressions.constant(false),
                product.createdAt
        );
    }

    private BooleanExpression productTypeEq(ProductType productType) {
        return productType != null
                ? product.type.eq(productType)
                : null;
    }

    private BooleanExpression lastVariantIdLt(Long lastVariantId) {
        return lastVariantId != null
                ? variant.id.lt(lastVariantId)
                : null;
    }
}
