package com.kakaotech.ott.ott.post.application.component;

import com.kakaotech.ott.ott.post.domain.model.PostType;
import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FreeImageLoader implements ImageLoader{

    private final PostRepository postRepository;


    @Override
    public boolean supports(PostType type) {
        return type == PostType.FREE;
    }

    @Override
    public List<?> loadImages(Long postId) {
        return postRepository.findById(postId).getImages();
    }
}
