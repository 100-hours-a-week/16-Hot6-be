package com.kakaotech.ott.ott.productOrder.domain.repository;

import com.kakaotech.ott.ott.productOrder.infrastructure.entity.ProductOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductOrderJpaRepository extends JpaRepository<ProductOrderEntity, Long> {

    // 1️⃣ EntityGraph 사용해서 연관 엔티티(userEntity) 같이 조회
    @EntityGraph(attributePaths = {"userEntity"})
    @Query("""
        SELECT p
        FROM ProductOrderEntity p
        WHERE p.deletedAt IS NULL
          AND p.userEntity.id = :userId
          AND (:lastProductOrderId IS NULL OR p.id < :lastProductOrderId)
        ORDER BY p.id DESC
    """)
    Page<ProductOrderEntity> findUserAllProductOrders(
            @Param("userId") Long userId,
            @Param("lastProductOrderId") Long lastProductOrderId,
            Pageable pageable
    );

    Optional<ProductOrderEntity> findByIdAndUserEntity_IdAndDeletedAtIsNull(Long orderId, Long userId);

}
