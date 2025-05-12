package com.kakaotech.ott.ott.post.application.component;

import com.kakaotech.ott.ott.post.domain.model.PostType;

import java.util.List;

public interface ImageLoader {

    boolean supports(PostType type);
    List<?> loadImages(Long postId);
}
