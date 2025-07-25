package com.kakaotech.ott.ott.productOrder.domain.repository;

import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrderStatus;
import com.kakaotech.ott.ott.productOrder.infrastructure.entity.ProductOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
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

    Optional<ProductOrderEntity> findByIdAndUserEntity_IdAndDeletedAtIsNullAndStatusNot(Long orderId, Long userId, ProductOrderStatus status);

    Optional<ProductOrderEntity> findByIdAndDeletedAtIsNull(Long orderId);

    @Query("SELECT o FROM ProductOrderEntity o " +
            "WHERE o.orderedAt <= :threshold " +
            "AND o.status = 'DELIVERED' " +
            "AND o.deletedAt IS NULL")
    List<ProductOrderEntity> findOrdersToAutoConfirm(@Param("threshold") LocalDateTime threshold);

    @Query("SELECT o FROM ProductOrderEntity o " +
            "WHERE o.orderedAt <= :threshold " +
            "AND o.status = 'PENDING' " +
            "AND o.deletedAt IS NULL")
    List<ProductOrderEntity> findOrdersToAutoDelete(@Param("threshold") LocalDateTime threshold);

    @Query("""
        SELECT COUNT(p) > 0 FROM ProductOrderEntity p
            WHERE p.userEntity.id = :userId
                AND p.orderFingerprint = :fingerprint
                AND p.status = 'PENDING'
    """)
    boolean existsByUserEntityIdAndFingerprint(
            @Param("userId") Long userId,
            @Param("fingerprint") String fingerprint);
}
