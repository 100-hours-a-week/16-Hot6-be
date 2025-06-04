package com.kakaotech.ott.ott.productOrder.presentation.dto.response;

import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrderStaus;
import com.kakaotech.ott.ott.productOrder.presentation.dto.request.ServiceProductDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductOrderResponseDto {

    @NotNull
    private Long orderId;

    @NotNull
    private List<ServiceProductDto> products;

    @NotNull
    private int totalAmount;

    @NotNull
    private ProductOrderStaus status;

    @NotNull
    private LocalDateTime createdAt;
}
