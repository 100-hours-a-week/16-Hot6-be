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

    private ProductOrderStaus status;

    private int subtotalAmount;

    private int discountAmount;

    private LocalDateTime orderedAt;

    private LocalDateTime deletedAt;

    private static final ULID ulid = new ULID();

    public static ProductOrder createOrder(Long userId, int subtotalAmount, int discountAmount) {

        return ProductOrder.builder()
                .userId(userId)
                .orderNumber(ulid.nextULID())
                .status(ProductOrderStaus.ORDERED)
                .subtotalAmount(subtotalAmount)
                .discountAmount(discountAmount)
                .build();
    }

    public void pay() {
        if (this.status != ProductOrderStaus.ORDERED) throw new CustomException(ErrorCode.NOT_ORDERED_STATE);

        if (this.status == ProductOrderStaus.PAID) throw new CustomException(ErrorCode.ALREADY_PAID);

        this.status = ProductOrderStaus.PAID;
    }

    public void confirm() {
        if (this.status != ProductOrderStaus.DELIVERED) throw new CustomException(ErrorCode.NOT_DELIVERABLE_STATE);

        if (this.status == ProductOrderStaus.CONFIRMED) throw new CustomException(ErrorCode.ALREADY_CONFIRMED);

        this.status = ProductOrderStaus.CONFIRMED;
    }

    public void deliver() {
        if (this.status != ProductOrderStaus.PAID) throw new CustomException(ErrorCode.NOT_PAID_STATE);

        if (this.status == ProductOrderStaus.DELIVERED) throw new CustomException(ErrorCode.ALREADY_DELIVERED);

        this.status = ProductOrderStaus.DELIVERED;
    }

    public void deleteOrder() {
        if(this.deletedAt != null) throw new CustomException(ErrorCode.ALREADY_DELETED);

        this.deletedAt = LocalDateTime.now();
    }
}
