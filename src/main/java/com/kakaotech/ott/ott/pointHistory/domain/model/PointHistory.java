package com.kakaotech.ott.ott.pointHistory.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PointHistory {

    private Long id;
    private Long userId;

    private int amount;
    private int balanceAfter;

    private PointActionType type;

    private String description;

    private LocalDateTime createdAt;

    public PointHistory createPointHistory(Long userId, int amount, int balanceAfter, PointActionType type, String description) {

        return PointHistory.builder()
                .userId(userId)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .type(type)
                .description(description)
                .build();
    }
}
