package com.kakaotech.ott.ott.post.application.component;

import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageRepository;
import com.kakaotech.ott.ott.post.domain.model.PostType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AiImageLoader implements ImageLoader{

    private final AiImageRepository aiImageRepository;


    @Override
    public boolean supports(PostType type) {
        return type == PostType.AI;
    }

    @Override
    public List<?> loadImages(Long postId) {
        return List.of(aiImageRepository.findByPostId(postId));
    }
}
