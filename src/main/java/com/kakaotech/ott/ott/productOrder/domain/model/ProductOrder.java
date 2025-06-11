package com.kakaotech.ott.ott.productOrder.domain.model;

import de.huxhorn.sulky.ulid.ULID;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ProductOrder {

    private Long id;

    private Long userId;

    private String orderNumber;

    private ProductOrderStatus status;

    private int subtotalAmount;

    private int discountAmount;

    private LocalDateTime orderedAt;

    private LocalDateTime deliveredAt;

    private LocalDateTime confirmedAt;

    private LocalDateTime canceledAt;

    private LocalDateTime refundedAt;

    private LocalDateTime deletedAt;

    private static final ULID ulid = new ULID();

    public static ProductOrder createOrder(Long userId, int subtotalAmount, int discountAmount) {

        return ProductOrder.builder()
                .userId(userId)
                .orderNumber(ulid.nextULID())
                .status(ProductOrderStatus.PENDING)
                .subtotalAmount(subtotalAmount)
                .discountAmount(discountAmount)
                .build();
    }

    public void pay() {
        if (this.status != ProductOrderStatus.PENDING)
            throw new CustomException(ErrorCode.NOT_PENDING_STATE);

        this.status = ProductOrderStatus.PAID;
    }

    public void confirm() {
        if (!(this.status == ProductOrderStatus.DELIVERED || this.status == ProductOrderStatus.PARTIALLY_REFUNDED))
            throw new CustomException(ErrorCode.NOT_DELIVERED_STATE);

        this.confirmedAt = LocalDateTime.now();
        this.status = ProductOrderStatus.CONFIRMED;
    }

    public void deliver() {
        if (!(this.status == ProductOrderStatus.PAID || this.status == ProductOrderStatus.PARTIALLY_CANCELED))
            throw new CustomException(ErrorCode.NOT_PAID_STATE);

        this.deletedAt = LocalDateTime.now();
        this.status = ProductOrderStatus.DELIVERED;
    }

    public void cancel() {
        if (!(this.status == ProductOrderStatus.PAID || this.status == ProductOrderStatus.PARTIALLY_CANCELED))
            throw new CustomException(ErrorCode.NOT_CANCELABLE_STATE);

        this.canceledAt = LocalDateTime.now();
        this.status = ProductOrderStatus.CANCELED;
    }

    public void partialCancel() {
        if (!(this.status == ProductOrderStatus.PAID || this.status == ProductOrderStatus.PARTIALLY_CANCELED))
            throw new CustomException(ErrorCode.NOT_CANCELABLE_STATE);

        this.status = ProductOrderStatus.PARTIALLY_CANCELED;
    }

    public void cancelRefund() {
        if (!(this.status == ProductOrderStatus.DELIVERED || this.status == ProductOrderStatus.PARTIALLY_REFUNDED))
            throw new CustomException(ErrorCode.NOT_DELIVERED_STATE);

        this.status = ProductOrderStatus.REFUNDED;
    }

    public void refund() {
        if (!(this.status == ProductOrderStatus.DELIVERED || this.status == ProductOrderStatus.PARTIALLY_REFUNDED))
            throw new CustomException(ErrorCode.NOT_DELIVERED_STATE);

        this.refundedAt = LocalDateTime.now();
        this.status = ProductOrderStatus.REFUNDED;
    }


    public void deleteOrder() {
        if(this.deletedAt != null) throw new CustomException(ErrorCode.ALREADY_DELETED);

        this.deletedAt = LocalDateTime.now();
    }
}
