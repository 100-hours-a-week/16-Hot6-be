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

    private PointActionReason description;

    private LocalDateTime createdAt;

    public static PointHistory createPointHistory(Long userId, int amount, int balanceAfter, PointActionType type, PointActionReason description) {

        return PointHistory.builder()
                .userId(userId)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .type(type)
                .description(description)
                .build();
    }
}
// 포인트 넣기
//insert into point_history (user_id, amount, balance_after, type, description)
//values (2, 10000000, 10000700, 'EARN', 'POST_CREATE');