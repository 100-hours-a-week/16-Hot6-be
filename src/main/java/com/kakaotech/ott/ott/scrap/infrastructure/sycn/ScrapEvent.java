package com.kakaotech.ott.ott.scrap.infrastructure.sycn;

import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ScrapEvent {
    private Long userId;
    private Long targetId;
    private ScrapType type;
    private String action;
}
