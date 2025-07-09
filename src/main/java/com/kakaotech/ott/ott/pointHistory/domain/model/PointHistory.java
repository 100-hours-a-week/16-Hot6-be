package com.kakaotech.ott.ott.pointHistory.domain.model;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
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

    public PointHistory deduct(int amount) {
        if (this.getBalanceAfter() < amount)
            throw new CustomException(ErrorCode.INSUFFICIENT_POINT_BALANCE);

        return createPointHistory(this.userId, amount, this.getBalanceAfter() - amount,
                PointActionType.DEDUCT, PointActionReason.PRODUCT_PURCHASE);
    }
}