package com.kakaotech.ott.ott.user.presentation.dto.response;

import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyScrapResponseDto {

    private List<Scraps> scraps;
    private Pagination pagination;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Scraps {

        private Long scrapId;
        private ScrapType type;
        private Long targetId;
        private String thumbnailUrl;
        private boolean scrapped;

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private int size;
        private Long lastScrapId;
        private boolean hasNext;
    }
}
