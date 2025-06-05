package com.kakaotech.ott.ott.productOrder.presentation.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyProductOrderHistoryListResponseDto {
    private List<MyProductOrderHistoryResponseDto> orders;
    private Pagination pagination;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Pagination {
        private final int size;
        private final Long lastOrderId;
        private final boolean hasNext;
    }
}

