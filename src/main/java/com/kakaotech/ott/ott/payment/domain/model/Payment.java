package com.kakaotech.ott.ott.payment.domain.model;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import de.huxhorn.sulky.ulid.ULID;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Payment {

    private Long id;
    private Long orderId;

    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;

    private int paymentAmount;
    private int refundedAmount;

    private String approveNumber;

    private LocalDateTime paidAt;
    private LocalDateTime lastRefundedAt;
    private LocalDateTime canceledAt;

    private static final ULID ulid = new ULID();

    public static Payment createPayment(Long orderId, PaymentMethod paymentMethod, int paymentAmount) {

        return Payment.builder()
                .orderId(orderId)
                .paymentMethod(paymentMethod)
                .paymentStatus(PaymentStatus.SUCCESS)
                .paymentAmount(paymentAmount)
                .refundedAmount(0)
                .approveNumber(ulid.nextULID())
                .build();
    }

    public void partialRefund(int refundedAmount, LocalDateTime lastRefundedAt) {
        this.paymentStatus = PaymentStatus.PARTIALLY_REFUNDED;
        this.refundedAmount = refundedAmount;
        this.canceledAt = lastRefundedAt;
    }

    public void refund(int refundedAmount, LocalDateTime lastRefundedAt) {
        this.paymentStatus = PaymentStatus.REFUNDED;
        this.refundedAmount = refundedAmount;
        this.canceledAt = lastRefundedAt;
        this.lastRefundedAt = lastRefundedAt;
    }

}
