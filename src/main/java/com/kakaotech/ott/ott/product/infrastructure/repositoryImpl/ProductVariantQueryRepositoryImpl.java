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

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductVariantQueryRepositoryImpl implements ProductVariantQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final QProductVariantEntity variant = QProductVariantEntity.productVariantEntity;
    private final QProductEntity product = QProductEntity.productEntity;
    private final QProductImageEntity image = QProductImageEntity.productImageEntity;
    private final QProductPromotionEntity promotion = QProductPromotionEntity.productPromotionEntity;

    @Override
    public ProductListResponseDto findProductListByCursor(Long userId, ProductType productType, PromotionType promotionType, Long lastVariantId, int size) {

        List<ProductListResponseDto.Products> result = queryFactory
                .select(productListProjection())
                .distinct()
                .from(variant)
                .join(variant.productEntity, product)
                .leftJoin(variant.images, image).on(image.sequence.eq(1))
                .leftJoin(variant.promotions, promotion)
                .where(buildWhereConditions(productType, promotionType, lastVariantId))
                .groupBy(variant.id, product.id, promotion.id)
                .orderBy(promotionType != null ?
                        promotion.endAt.asc().nullsLast() :
                        variant.id.desc())
                .limit(size)
                .fetch();

        boolean hasNext = result.size() == size;
        Long nextLastVariantId = hasNext ? result.get(result.size() - 1).getVariantId() : null;

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

    private ConstructorExpression<ProductListResponseDto.Products> productListProjection() {
        return Projections.constructor(ProductListResponseDto.Products.class,
                product.id,
                product.name,
                product.type.stringValue(),
                variant.id,
                variant.name,
                image.imageUuid.max(),
                variant.price,
                promotion.discountPrice,
                promotion.rate,
                promotion.startAt,
                promotion.endAt,
                variant.totalQuantity.subtract(variant.reservedQuantity).subtract(variant.soldQuantity),
                promotion.id.isNotNull(),
                Expressions.constant(false),
                product.createdAt
        );
    }

    private BooleanExpression[] buildWhereConditions(ProductType productType,
                                                     PromotionType promotionType,
                                                     Long lastVariantId) {
        return new BooleanExpression[]{
                productTypeEq(productType),
                promotionTypeConditional(promotionType),
                lastVariantIdLt(lastVariantId)
        };
    }

    private BooleanExpression productTypeEq(ProductType productType) {
        return productType != null ? product.type.eq(productType) : null;
    }

    private BooleanExpression promotionTypeConditional(PromotionType promotionType) {
        if (promotionType != null) {
            return promotion.type.eq(promotionType)
                    .and(promotion.status.in(PromotionStatus.ACTIVE, PromotionStatus.SOLD_OUT))
                    .and(promotion.startAt.before(LocalDateTime.now()))
                    .and(promotion.endAt.after(LocalDateTime.now()));
        } else {
            return promotion.id.isNull(); // 일반 상품만 조회
        }
    }

    private BooleanExpression lastVariantIdLt(Long lastVariantId) {
        return lastVariantId != null ? variant.id.lt(lastVariantId) : null;
    }
}
