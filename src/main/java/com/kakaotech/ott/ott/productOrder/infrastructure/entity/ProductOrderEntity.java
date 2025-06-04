package com.kakaotech.ott.ott.productOrder.infrastructure.entity;

import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrderStaus;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "product_orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class ProductOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Column(name = "order_number", nullable = false, length = 26)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ProductOrderStaus status;

    @Column(name = "subtotal_amount", nullable = false)
    private int subtotalAmount;

    @Column(name = "discount_amount", nullable = false)
    private int discountAmount;

    @CreatedDate
    @Column(name = "ordered_at", nullable = false, updatable = false)
    private LocalDateTime orderedAt;

    @Column(name = "deleted_at", nullable = false)
    private LocalDateTime deletedAt;

    public ProductOrder toDomain() {

        return ProductOrder.builder()
                .id(this.id)
                .userId(this.userEntity.getId())
                .orderNumber(this.orderNumber)
                .status(this.status)
                .subtotalAmount(this.subtotalAmount)
                .discountAmount(this.discountAmount)
                .orderedAt(this.orderedAt)
                .deletedAt(this.deletedAt)
                .build();
    }

    public static ProductOrderEntity from(ProductOrder productOrder, UserEntity userEntity) {

        return ProductOrderEntity.builder()
                .userEntity(userEntity)
                .orderNumber(productOrder.getOrderNumber())
                .status(productOrder.getStatus())
                .subtotalAmount(productOrder.getSubtotalAmount())
                .discountAmount(productOrder.getDiscountAmount())
                .deletedAt(productOrder.getDeletedAt())
                .build();
    }



}
