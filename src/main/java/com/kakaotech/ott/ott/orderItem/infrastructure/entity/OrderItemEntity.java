package com.kakaotech.ott.ott.orderItem.infrastructure.entity;

import com.kakaotech.ott.ott.orderItem.domain.model.CancelReason;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductPromotionEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductVariantEntity;
import com.kakaotech.ott.ott.productOrder.infrastructure.entity.ProductOrderEntity;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;
import com.kakaotech.ott.ott.orderItem.domain.model.RefundReason;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private ProductOrderEntity productOrderEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variants_id")
    private ProductVariantEntity productVariantEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private ProductPromotionEntity productPromotionEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderItemStatus status;

    @Column(name = "original_price", nullable = false)
    private int originalPrice;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "discount_amount", nullable = false)
    private int discountAmount;

    @Column(name = "final_price", nullable = false)
    private int finalPrice;

    @Column(name = "refund_amount")
    private int refundAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_reason")
    private RefundReason refundReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancel_reason")
    private CancelReason cancelReason;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "refund_requested_at")
    private LocalDateTime refundRequestedAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    public OrderItem toDomain() {

        return OrderItem.builder()
                .id(this.id)
                .orderId(this.productOrderEntity.getId())
                .variantsId(this.productVariantEntity.getId())
                .promotionId(this.productPromotionEntity != null ? this.productPromotionEntity.getId() : null)
                .status(this.status)
                .originalPrice(this.originalPrice)
                .quantity(this.quantity)
                .discountAmount(this.discountAmount)
                .finalPrice(this.finalPrice)
                .refundAmount(this.refundAmount)
                .refundReason(this.refundReason)
                .cancelReason(this.cancelReason)
                .canceledAt(this.canceledAt)
                .refundedAt(this.refundedAt)
                .build();
    }

    public static OrderItemEntity from(OrderItem orderItem, ProductOrderEntity productOrderEntity, ProductVariantEntity productVariantEntity, ProductPromotionEntity productPromotionEntity) {

        return OrderItemEntity.builder()
                .productOrderEntity(productOrderEntity)
                .productVariantEntity(productVariantEntity)
                .productPromotionEntity(productPromotionEntity)
                .status(orderItem.getStatus())
                .originalPrice(orderItem.getOriginalPrice())
                .quantity(orderItem.getQuantity())
                .discountAmount(orderItem.getDiscountAmount())
                .finalPrice(orderItem.getFinalPrice())
                .build();
    }

    public void fail(OrderItem item) {
        this.status = item.getStatus();
    }

    public void pay(OrderItem item) {
        this.status = item.getStatus();
    }

    public void cancel(OrderItem item) {
        this.status = item.getStatus();
        this.refundAmount = item.getFinalPrice() * item.getQuantity();
        this.cancelReason = item.getCancelReason();
        this.canceledAt = item.getCanceledAt();
    }

    public void refundRequest(OrderItem item) {

        this.status = item.getStatus();
        this.refundReason = item.getRefundReason();
        this.refundRequestedAt = item.getRefundRequestedAt();
    }

    public void refund(OrderItem item) {

        this.status = item.getStatus();
        this.refundAmount = item.getRefundAmount();
        this.refundedAt = item.getRefundedAt();
    }

    public void delivery(OrderItem item) {

        this.status = item.getStatus();
    }

    public void confirm(OrderItem item) {
        this.status = item.getStatus();
    }
}
