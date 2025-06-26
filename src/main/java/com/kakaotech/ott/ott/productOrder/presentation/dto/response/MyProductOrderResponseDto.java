package com.kakaotech.ott.ott.productOrder.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;
import com.kakaotech.ott.ott.orderItem.domain.model.RefundReason;
import com.kakaotech.ott.ott.payment.domain.model.PaymentMethod;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrderStatus;
import com.kakaotech.ott.ott.util.KstDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyProductOrderResponseDto {

    private OrderInfo order;
    private List<ProductInfo> products;
    private UserInfo user;
    private PaymentInfo payment;
    private RefundInfo refund;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderInfo {
        private Long id;
        private ProductOrderStatus status;
        private String orderNumber;

        @JsonProperty("orderedAt")
        private KstDateTime orderedAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductInfo {
        private Long id;
        private Long productId;
        private String name;
        private OrderItemStatus status;
        private String imagePath;
        private int price;
        private int quantity;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String nicknameKakao;
        private String email;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentInfo {
        private PaymentMethod paymentMethod;   // TODO: 타입 ENUM으로 바꿔야됨
        private int originalAmount;
        private int paymentAmount;
        private int discountAmount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundInfo {
        private PaymentMethod refundMethod;
        private int refundAmount;
    }
}
