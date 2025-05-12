package com.kakaotech.ott.ott.user.presentation.dto.response;

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
    private int size;
    private Long lastPostId;
    private boolean hasNext;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageDto {
        private Long aiImageId;
        private String beforeImagePath;
        private String afterImagePath;
        private LocalDateTime createdAt;
    }
}
