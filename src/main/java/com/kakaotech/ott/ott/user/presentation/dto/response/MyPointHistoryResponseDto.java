package com.kakaotech.ott.ott.user.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointActionReason;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointActionType;
import com.kakaotech.ott.ott.util.KstDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyPointHistoryResponseDto {

    private List<PointInfo> point;
    private MyPointHistoryResponseDto.Pagination pagination;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointInfo {
        private Long historyId;
        private PointActionReason description;
        private PointActionType type;
        private int amount;
        private int balance_after;

        @JsonProperty("createdAt")
        private KstDateTime createdAt;

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private int size;
        private Long lastPointHistoryId;
        private boolean hasNext;
    }
}
