package com.kakaotech.ott.ott.like.infrastructure.sync;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LikeEvent {
    private Long userId;
    private Long postId;
    private String action;
}
