package com.kakaotech.ott.ott.productOrder.presentation.dto.response;

import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyProductOrderHistoryResponseDto {

    private Long orderId;
    private ProductOrderStatus orderStatus;
    private LocalDateTime orderedAt;
    private List<ProductDto> products;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductDto {
        private Long productId;
        private String status;
        private String productName;
        private int quantity;
        private int amount;
        private String imagePath;
    }

}