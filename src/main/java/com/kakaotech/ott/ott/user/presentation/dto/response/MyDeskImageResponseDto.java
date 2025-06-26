package com.kakaotech.ott.ott.user.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaotech.ott.ott.post.domain.model.MyDeskState;
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
public class MyDeskImageResponseDto {

    private List<ImageDto> images;
    private MyDeskState myDeskState;
    private MyDeskImageResponseDto.Pagination pagination;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageDto {
            private Long aiImageId;
        private String beforeImagePath;
        private String afterImagePath;

        @JsonProperty("createdAt")
        private KstDateTime createdAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private int size;
        private Long lastAiImageId;
        private boolean hasNext;
    }
}
