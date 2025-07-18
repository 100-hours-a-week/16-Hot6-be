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
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductVariantQueryRepositoryImpl implements ProductVariantQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QProductVariantEntity variant    = QProductVariantEntity.productVariantEntity;
    private final QProductEntity        product    = QProductEntity.productEntity;
    private final QProductImageEntity   image      = QProductImageEntity.productImageEntity;
    private final QProductPromotionEntity promotion = QProductPromotionEntity.productPromotionEntity;

    @Override
    public ProductListResponseDto findProductListByCursor(
            Long userId,
            ProductType productType,
            PromotionType promotionType,
            Long lastVariantId,
            int size
    ) {
        LocalDateTime now = LocalDateTime.now();
        List<ProductListResponseDto.Products> products = fetchProducts(now, productType, promotionType, lastVariantId, size);
        return buildResponse(products, size);
    }

    private List<ProductListResponseDto.Products> fetchProducts(
            LocalDateTime now,
            ProductType productType,
            PromotionType promotionType,
            Long lastVariantId,
            int size
    ) {
        JPAQuery<ProductListResponseDto.Products> baseQuery = queryFactory
                .select(productListProjection())
                .distinct()
                .from(variant)
                .join(variant.productEntity, product)
                .leftJoin(variant.images, image).on(image.sequence.eq(1));

        if (promotionType != null) {
            applyPromotionFilter(baseQuery, now, lastVariantId);
        } else {
            applyDefaultFilter(baseQuery, lastVariantId);
        }

        return baseQuery
                .limit(size)
                .fetch();
    }

    private void applyPromotionFilter(JPAQuery<ProductListResponseDto.Products> query,
                                      LocalDateTime now,
                                      Long lastVariantId) {
        query
                .innerJoin(variant.promotions, promotion)
                .on(
                        promotion.status.in(PromotionStatus.ACTIVE, PromotionStatus.SOLD_OUT)
                                .and(promotion.endAt.after(now))
                )
                .where(
                        variant.isOnPromotion.eq(true),
                        variant.id.lt(lastVariantId)
                )
                .orderBy(promotion.endAt.asc().nullsLast());
    }

    private void applyDefaultFilter(JPAQuery<ProductListResponseDto.Products> query,
                                    Long lastVariantId) {
        query
                .where(
                        variant.isOnPromotion.eq(false),
                        variant.id.lt(lastVariantId)
                )
                .orderBy(variant.id.desc());
    }

    private ProductListResponseDto buildResponse(List<ProductListResponseDto.Products> list, int size) {
        boolean hasNext = list.size() == size;
        Long nextLastId = hasNext ? list.get(list.size() - 1).getVariantId() : null;

        ProductListResponseDto.Pagination pagination = ProductListResponseDto.Pagination.builder()
                .size(size)
                .lastVariantId(nextLastId)
                .hasNext(hasNext)
                .build();

        return ProductListResponseDto.builder()
                .products(list)
                .pagination(pagination)
                .build();
    }

    private ConstructorExpression<ProductListResponseDto.Products> productListProjection() {
        return Projections.constructor(
                ProductListResponseDto.Products.class,
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
                variant.totalQuantity
                        .subtract(variant.reservedQuantity)
                        .subtract(variant.soldQuantity),
                variant.isOnPromotion,
                Expressions.constant(false),
                product.createdAt
        );
    }
}
