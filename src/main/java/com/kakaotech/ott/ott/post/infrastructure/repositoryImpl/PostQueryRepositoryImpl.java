package com.kakaotech.ott.ott.post.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.aiImage.infrastructure.entity.QAiImageEntity;
import com.kakaotech.ott.ott.like.infrastructure.entity.QLikeEntity;
import com.kakaotech.ott.ott.post.application.component.PostDtoMapper;
import com.kakaotech.ott.ott.post.domain.model.PostType;
import com.kakaotech.ott.ott.post.domain.repository.PostQueryRepository;
import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import com.kakaotech.ott.ott.post.infrastructure.entity.QPostEntity;
import com.kakaotech.ott.ott.post.presentation.dto.response.PostAllResponseDto;
import com.kakaotech.ott.ott.postImage.entity.QPostImageEntity;
import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import com.kakaotech.ott.ott.scrap.infrastructure.entity.QScrapEntity;
import com.kakaotech.ott.ott.user.infrastructure.entity.QUserEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
class PostQueryRepositoryImpl implements PostQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final PostDtoMapper postDtoMapper;

    @Override
    public PostAllResponseDto getAllPost(Long userId, String category, String sort, int size,
                                         Long lastPostId, Integer lastLikeCount, Long lastViewCount, Double lastWeightCount) {
        List<PostEntity> postEntities = fetchPosts(category, sort, size, lastPostId, lastLikeCount, lastViewCount, lastWeightCount);
        List<Long> postIds = extractPostIds(postEntities);

        Map<Long, Boolean> likedMap = fetchLikedMap(userId, postIds);
        Map<Long, Boolean> scrappedMap = fetchScrappedMap(userId, postIds);
        Map<Long, String> thumbnailMap = fetchThumbnailMap(postIds);

        List<PostAllResponseDto.Posts> posts = convertToPostDtos(postEntities, size, likedMap, scrappedMap, thumbnailMap);
        PostAllResponseDto.Pagination pagination = createPagination(posts, size);

        return new PostAllResponseDto(posts, pagination);
    }

    private List<PostEntity> fetchPosts(String category, String sort, int size,
                                        Long lastPostId, Integer lastLikeCount, Long lastViewCount, Double lastWeightCount) {
        QPostEntity post = QPostEntity.postEntity;
        QUserEntity user = QUserEntity.userEntity;

        var query = queryFactory.selectFrom(post)
                .join(post.userEntity, user).fetchJoin();

        if (category != null) {
            query.where(post.type.eq(PostType.valueOf(category)));
        }

        applyCursorPaging(query, sort, lastPostId, lastLikeCount, lastViewCount, lastWeightCount);

        return query.limit(size + 1).fetch();
    }

    private List<Long> extractPostIds(List<PostEntity> postEntities) {
        return postEntities.stream().map(PostEntity::getId).collect(Collectors.toList());
    }

    private Map<Long, Boolean> fetchLikedMap(Long userId, List<Long> postIds) {
        if (userId == null || postIds.isEmpty()) return Collections.emptyMap();

        QLikeEntity like = QLikeEntity.likeEntity;
        return queryFactory.selectFrom(like)
                .where(like.userEntity.id.eq(userId).and(like.postEntity.id.in(postIds)))
                .fetch()
                .stream()
                .collect(Collectors.toMap(l -> l.getPostEntity().getId(), l -> true));
    }

    private Map<Long, Boolean> fetchScrappedMap(Long userId, List<Long> postIds) {
        if (userId == null || postIds.isEmpty()) return Collections.emptyMap();

        QScrapEntity scrap = QScrapEntity.scrapEntity;
        return queryFactory.selectFrom(scrap)
                .where(scrap.userEntity.id.eq(userId)
                        .and(scrap.type.eq(ScrapType.POST))
                        .and(scrap.targetId.in(postIds)))
                .fetch()
                .stream()
                .collect(Collectors.toMap(s -> s.getTargetId(), s -> true));
    }

    private Map<Long, String> fetchThumbnailMap(List<Long> postIds) {
        Map<Long, String> thumbnailMap = new HashMap<>();
        QPostImageEntity postImage = QPostImageEntity.postImageEntity;
        QAiImageEntity aiImage = QAiImageEntity.aiImageEntity;

        List<Tuple> aiImages = queryFactory.select(aiImage.postId, aiImage.afterImagePath)
                .from(aiImage)
                .where(aiImage.postId.in(postIds))
                .fetch();

        Set<Long> aiPostIds = new HashSet<>();

        for (Tuple tuple : aiImages) {
            Long postId = tuple.get(aiImage.postId);
            thumbnailMap.put(postId, tuple.get(aiImage.afterImagePath));
            aiPostIds.add(postId);
        }

        // AI 이미지가 없는 게시글만 필터링
        List<Long> noAiImagePostIds = postIds.stream()
                .filter(id -> !aiPostIds.contains(id))
                .toList();

        if (!noAiImagePostIds.isEmpty()) {
            queryFactory.select(postImage.postEntity.id, postImage.imageUuid)
                    .from(postImage)
                    .where(postImage.postEntity.id.in(noAiImagePostIds))
                    .orderBy(postImage.sequence.asc())
                    .fetch()
                    .forEach(tuple -> thumbnailMap.putIfAbsent(
                            tuple.get(postImage.postEntity.id), tuple.get(postImage.imageUuid))
                    );
        }
        return thumbnailMap;
    }

    private List<PostAllResponseDto.Posts> convertToPostDtos(List<PostEntity> postEntities, int size,
                                                             Map<Long, Boolean> likedMap,
                                                             Map<Long, Boolean> scrappedMap,
                                                             Map<Long, String> thumbnailMap) {
        return postEntities.stream()
                .limit(size)
                .map(p -> postDtoMapper.toDto(
                        p,
                        thumbnailMap.getOrDefault(p.getId(), ""),
                        likedMap.getOrDefault(p.getId(), false),
                        scrappedMap.getOrDefault(p.getId(), false)))
                .collect(Collectors.toList());
    }

    private PostAllResponseDto.Pagination createPagination(List<PostAllResponseDto.Posts> posts, int size) {
        boolean hasNext = posts.size() >= size;
        Long nextLastId = hasNext ? posts.get(posts.size() - 1).getPostId() : null;
        Long nextLastLikeCount = hasNext ? posts.get(posts.size() - 1).getLikeCount() : null;
        Long nextLastViewCount = hasNext ? posts.get(posts.size() - 1).getViewCount() : null;
        Double nextLastWeightCount = hasNext ? posts.get(posts.size() - 1).getWeightCount() : null;

        return new PostAllResponseDto.Pagination(
                size, nextLastId, nextLastLikeCount, nextLastViewCount, nextLastWeightCount, hasNext);
    }

    private void applyCursorPaging(com.querydsl.jpa.impl.JPAQuery<?> query, String sort, Long lastPostId,
                                   Integer lastLikeCount, Long lastViewCount, Double lastWeightCount) {
        QPostEntity post = QPostEntity.postEntity;
        BooleanBuilder builder = new BooleanBuilder();

        if (lastPostId != null) {
            switch (sort) {
                case "LIKE" -> builder.and(post.likeCount.loe(lastLikeCount)).and(post.id.lt(lastPostId));
                case "VIEW" -> builder.and(post.viewCount.loe(lastViewCount)).and(post.id.lt(lastPostId));
                case "POPULAR" -> builder.and(post.weight.loe(lastWeightCount)).and(post.id.lt(lastPostId));
                default -> builder.and(post.id.lt(lastPostId));
            }
        }

        query.where(builder);

        switch (sort) {
            case "LIKE" -> query.orderBy(post.likeCount.desc(), post.id.desc());
            case "VIEW" -> query.orderBy(post.viewCount.desc(), post.id.desc());
            case "POPULAR" -> query.orderBy(post.weight.desc(), post.id.desc());
            default -> query.orderBy(post.id.desc());
        }
    }
}