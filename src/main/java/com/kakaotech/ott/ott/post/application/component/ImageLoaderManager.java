package com.kakaotech.ott.ott.post.application.component;

import com.kakaotech.ott.ott.post.domain.model.PostType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageLoaderManager {

    private final List<ImageLoader> imageLoaderList;

    public List<?> loadImages(PostType type, Long postId) {
        return imageLoaderList.stream()
                .filter(loader -> loader.supports(type))
                .findFirst()
                .orElseThrow()
                .loadImages(postId);
    }
}
