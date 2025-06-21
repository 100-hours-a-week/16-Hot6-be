package com.kakaotech.ott.ott.admin.presentation.dto.response;

import com.kakaotech.ott.ott.orderItem.domain.model.RefundReason;
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

    List<RefundedProduct> refundedProducts;
    List<PaidProduct> paidProducts;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundedProduct {

        private Long variantsId;
        private Long promotionId;
        private String userName;
        private String productName;
        private int purchaseQuantity;
        private int paidAmount;
        private RefundReason refundReason;
        private LocalDateTime refundedAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaidProduct {

        private Long variantsId;
        private Long promotionId;
        private String userName;
        private String productName;
        private int purchaseQuantity;
        private int paidAmount;
        private LocalDateTime paidAt;
    }
}
