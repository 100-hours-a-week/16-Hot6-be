package com.kakaotech.ott.ott.orderItem.infrastructure.entity;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
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

    // 제품 엔티티 들어가야 된다.
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderItemStatus status;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "pending_product_status")
    private OrderItemStatus pendingProductStatus;

    @Column(name = "refund_amount")
    private int refundAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_reason")
    private RefundReason refundReason;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    public OrderItem toDomain() {

        return OrderItem.builder()
                .id(this.id)
                .orderId(this.productOrderEntity.getId())
                // feat: productId 변경해야됨
                .productId(1L)
                .status(this.status)
                .price(this.price)
                .quantity(this.quantity)
                .pendingProductStatus(this.pendingProductStatus)
                .refundAmount(this.refundAmount)
                .refundReason(this.refundReason)
                .canceledAt(this.canceledAt)
                .refundedAt(this.refundedAt)
                .build();
    }

    public static OrderItemEntity from(OrderItem orderItem, ProductOrderEntity productOrderEntity) {

        return OrderItemEntity.builder()
                .productOrderEntity(productOrderEntity)
                .productId(orderItem.getProductId())
                .status(orderItem.getStatus())
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .pendingProductStatus(orderItem.getPendingProductStatus())
                .build();
    }

    public void cancel(OrderItem item) {
        this.status = item.getStatus();
        this.refundAmount = item.getPrice() * item.getQuantity();
        this.refundReason = item.getRefundReason();
        this.refundedAt = item.getRefundedAt();
    }

    public void refund(OrderItem item) {

        this.status = item.getStatus();
        this.refundAmount = item.getPrice() * item.getQuantity();
        this.refundReason = item.getRefundReason();
        this.refundedAt = item.getRefundedAt();
    }

    public void confirm(OrderItem item) {
        this.status = item.getStatus();
    }

    public void setPendingProductStatus(OrderItemStatus pendingProductStatus) {
        this.pendingProductStatus = pendingProductStatus;
    }
}
