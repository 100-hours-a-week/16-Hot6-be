package com.kakaotech.ott.ott.post.application.component;

import com.kakaotech.ott.ott.aiImage.infrastructure.entity.QAiImageEntity;
import com.kakaotech.ott.ott.like.infrastructure.entity.QLikeEntity;
import com.kakaotech.ott.ott.post.domain.model.PostType;
import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import com.kakaotech.ott.ott.post.presentation.dto.response.PostAllResponseDto;
import com.kakaotech.ott.ott.postImage.entity.QPostImageEntity;
import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import com.kakaotech.ott.ott.scrap.infrastructure.entity.QScrapEntity;
import com.kakaotech.ott.ott.util.KstDateTime;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostDtoMapper {
    private final JPAQueryFactory queryFactory;

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

