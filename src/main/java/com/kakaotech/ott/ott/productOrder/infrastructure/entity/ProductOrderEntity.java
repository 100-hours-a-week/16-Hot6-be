package com.kakaotech.ott.ott.productOrder.infrastructure.entity;

import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrderStatus;
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
    private ProductOrderStatus status;

    @Column(name = "subtotal_amount", nullable = false)
    private int subtotalAmount;

    @Column(name = "discount_amount", nullable = false)
    private int discountAmount;

    @CreatedDate
    @Column(name = "ordered_at", nullable = false, updatable = false)
    private LocalDateTime orderedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

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
                .deliveredAt(this.deliveredAt)
                .confirmedAt(this.confirmedAt)
                .canceledAt(this.canceledAt)
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
                .deliveredAt(productOrder.getDeliveredAt())
                .confirmedAt(productOrder.getConfirmedAt())
                .canceledAt(productOrder.getCanceledAt())
                .deletedAt(productOrder.getDeletedAt())
                .build();
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setStatus(ProductOrderStatus productOrderStatus) {
        this.status = productOrderStatus;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public void setCanceledAt(LocalDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }

}
