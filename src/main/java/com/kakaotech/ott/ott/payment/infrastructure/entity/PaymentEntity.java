package com.kakaotech.ott.ott.payment.infrastructure.entity;

import com.kakaotech.ott.ott.payment.domain.model.Payment;
import com.kakaotech.ott.ott.payment.domain.model.PaymentMethod;
import com.kakaotech.ott.ott.payment.domain.model.PaymentStatus;
import com.kakaotech.ott.ott.productOrder.infrastructure.entity.ProductOrderEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private ProductOrderEntity productOrderEntity;

    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_amount", nullable = false)
    private int paymentAmount;

    @Column(name = "refunded_amount", nullable = false)
    private int refundedAmount;

    @Column(name = "approve_number", nullable = false)
    private String approveNumber;

    @CreatedDate
    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;

    @Column(name = "last_refunded_at")
    private LocalDateTime lastRefundedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    public static PaymentEntity from(Payment payment, ProductOrderEntity productOrderEntity) {

        return PaymentEntity.builder()
                .productOrderEntity(productOrderEntity)
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .paymentAmount(payment.getPaymentAmount())
                .refundedAmount(payment.getRefundedAmount())
                .approveNumber(payment.getApproveNumber())
                .paidAt(payment.getPaidAt())
                .lastRefundedAt(payment.getLastRefundedAt())
                .canceledAt(payment.getCanceledAt())
                .build();
    }

    public Payment toDomain() {

        return Payment.builder()
                .id(this.getId())
                .orderId(this.getProductOrderEntity().getId())
                .paymentMethod(this.getPaymentMethod())
                .paymentStatus(this.getPaymentStatus())
                .paymentAmount(this.getPaymentAmount())
                .refundedAmount(this.getRefundedAmount())
                .approveNumber(this.getApproveNumber())
                .paidAt(this.getPaidAt())
                .lastRefundedAt(this.getLastRefundedAt())
                .canceledAt(this.getCanceledAt())
                .build();
    }
}
