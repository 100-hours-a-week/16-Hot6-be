package com.kakaotech.ott.ott.post.application.component;

import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import com.kakaotech.ott.ott.post.presentation.dto.response.PostAllResponseDto;
import com.kakaotech.ott.ott.util.KstDateTime;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PostDtoMapper {
    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true)
    public PostAllResponseDto.Posts toDto(PostEntity post, String thumbnail, boolean liked, boolean scrapped) {
        return new PostAllResponseDto.Posts(
                post.getId(),
                post.getTitle(),
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
