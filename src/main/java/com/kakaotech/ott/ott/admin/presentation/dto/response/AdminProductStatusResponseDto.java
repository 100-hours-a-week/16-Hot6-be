package com.kakaotech.ott.ott.admin.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaotech.ott.ott.orderItem.domain.model.RefundReason;
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
public class AdminProductStatusResponseDto {

    private List<PaidProductOrder> paidProductOrders;
    private List<RefundedOrderItems> refundedOrderItems;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaidProductOrder {

        private Long productOrderId;
        private List<PaidOrderItem> refundedOrderItems;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class PaidOrderItem {

            private Long orderItemId;
            private Long promotionId;
            private String userName;
            private String productName;
            private int purchaseQuantity;
            private int paidAmount;

            @JsonProperty("paidAt")
            private KstDateTime paidAt;
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundedOrderItems {
        private Long orderItemId;
        private Long promotionId;
        private String userName;
        private String productName;
        private int purchaseQuantity;
        private int paidAmount;
        private RefundReason refundReason;

        @JsonProperty("refundedAt")
        private KstDateTime refundedAt;
    }
}
