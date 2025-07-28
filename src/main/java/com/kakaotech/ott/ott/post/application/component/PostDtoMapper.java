package com.kakaotech.ott.ott.post.application.component;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImageConcept;
import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import com.kakaotech.ott.ott.post.presentation.dto.response.PostAllResponseDto;
import com.kakaotech.ott.ott.util.KstDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PostDtoMapper {

    @Transactional(readOnly = true)
    public PostAllResponseDto.Posts toDto(PostEntity post, AiImageConcept concept, String thumbnail, boolean liked, boolean scrapped) {
        return new PostAllResponseDto.Posts(
                post.getId(),
                post.getTitle(),
                concept,
                new PostAllResponseDto.PostAuthorResponseDto(
                        post.getUserEntity().getNicknameCommunity(),
                        post.getUserEntity().getImagePath()
                ),
                thumbnail,
                post.getLikeCount(),
                post.getCommentCount(),
                post.getViewCount(),
                post.getWeight(),
                new KstDateTime(post.getCreatedAt()),
                liked,
                scrapped
        );
    }
}
