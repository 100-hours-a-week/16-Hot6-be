package com.kakaotech.ott.ott.payment.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaotech.ott.ott.orderItem.domain.model.RefundReason;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestDto {

    @NotEmpty(message = "환불 요청 상품은 최소 1개 이상이어야 합니다")
    @Valid
    private List<RefundItemRequest> refundItems;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundItemRequest {
        @NotNull(message = "품목 ID는 필수입니다")
        @JsonProperty("orderItemId")
        private Long orderItemId;

        @NotNull(message = "환불 사유는 필수입니다")
        @JsonProperty("reason")
        private RefundReason reason;
    }
}
