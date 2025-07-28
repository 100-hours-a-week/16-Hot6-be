package com.kakaotech.ott.ott.post.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImageConcept;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.QAiImageEntity;
import com.kakaotech.ott.ott.like.infrastructure.entity.QLikeEntity;
import com.kakaotech.ott.ott.post.application.component.PostDtoMapper;
import com.kakaotech.ott.ott.post.domain.model.PostType;
import com.kakaotech.ott.ott.post.domain.repository.PostQueryRepository;
import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import com.kakaotech.ott.ott.post.infrastructure.entity.QPostEntity;
import com.kakaotech.ott.ott.post.presentation.dto.response.PostAllResponseDto;
import com.kakaotech.ott.ott.post.presentation.dto.response.ThumbnailAndConceptMap;
import com.kakaotech.ott.ott.postImage.entity.QPostImageEntity;
import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import com.kakaotech.ott.ott.scrap.infrastructure.entity.QScrapEntity;
import com.kakaotech.ott.ott.scrap.infrastructure.entity.ScrapEntity;
import com.kakaotech.ott.ott.user.infrastructure.entity.QUserEntity;
import com.kakaotech.ott.ott.util.scheduler.LikeRedisKey;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
class PostQueryRepositoryImpl implements PostQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final PostDtoMapper postDtoMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public PostAllResponseDto getAllPost(Long userId, String category, String sort, int size,
                                         Long lastPostId, Integer lastLikeCount, Long lastViewCount, Double lastWeightCount) {
        List<PostEntity> postEntities = fetchPosts(category, sort, size, lastPostId, lastLikeCount, lastViewCount, lastWeightCount);
        List<Long> postIds = extractPostIds(postEntities);

        Map<Long, Boolean> likedMap = fetchLikedMap(userId, postIds);
        Map<Long, Boolean> scrappedMap = fetchScrappedMap(userId, postIds);

        ThumbnailAndConceptMap thumbnailAndConceptMap = fetchThumbnailAndConceptMap(postIds);
        Map<Long, String> thumbnailMap = thumbnailAndConceptMap.getThumbnailMap();
        Map<Long, AiImageConcept> conceptMap = thumbnailAndConceptMap.getConceptMap();

        List<PostAllResponseDto.Posts> posts = convertToPostDtos(postEntities, size, likedMap, scrappedMap, thumbnailMap, conceptMap);
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

        Map<Long, Boolean> result = new HashMap<>();
        List<Long> needDbLookup = new ArrayList<>();

        // 1) Redis 캐시에서 먼저 확인
        for (Long postId : postIds) {
            String key = LikeRedisKey.setLikeKey(postId);
            String member = String.valueOf(userId);

            Boolean isMember = redisTemplate.opsForSet().isMember(key, member);
            if (Boolean.TRUE.equals(isMember)) {
                result.put(postId, true);
            } else {
                needDbLookup.add(postId);
            }
        }

        // 2) Redis에 없으면 DB에서 조회
        if (!needDbLookup.isEmpty()) {
            QLikeEntity like = QLikeEntity.likeEntity;
            List<Long> likedPostIds = queryFactory.select(like.postEntity.id)
                    .from(like)
                    .where(like.userEntity.id.eq(userId)
                            .and(like.postEntity.id.in(needDbLookup))
                            .and(like.isActive.eq(true)))
                    .fetch();

            Set<Long> likedSet = new HashSet<>(likedPostIds);

            // 3) DB 결과를 응답용 Map에 담고, Redis에 다시 저장 (재캐시)
            for (Long postId : needDbLookup) {
                boolean liked = likedSet.contains(postId);
                result.put(postId, liked);

                if (liked) {
                    String key = LikeRedisKey.setLikeKey(postId);
                    redisTemplate.opsForSet().add(key, String.valueOf(userId));
                }
            }
        }

        return result;
    }


    private Map<Long, Boolean> fetchScrappedMap(Long userId, List<Long> postIds) {
        if (userId == null || postIds.isEmpty()) return Collections.emptyMap();

        Map<Long, Boolean> result = new HashMap<>();
        List<Long> needDbLookup = new ArrayList<>();

        String user = userId.toString();

        for (Long postId : postIds) {
            Boolean scrapped = redisTemplate.opsForSet().isMember(user, String.valueOf(postId));
            Boolean unscrapped = redisTemplate.opsForSet().isMember(user, String.valueOf(postId));

            if (Boolean.TRUE.equals(scrapped)) {
                result.put(postId, true);
            } else if (Boolean.TRUE.equals(unscrapped)) {
                result.put(postId, false);
            } else {
                needDbLookup.add(postId);
            }
        }


        // Redis에 없는 항목은 DB에서 조회
        if (!needDbLookup.isEmpty()) {
            QScrapEntity scrap = QScrapEntity.scrapEntity;
            List<ScrapEntity> scraps = queryFactory.selectFrom(scrap)
                    .where(scrap.userEntity.id.eq(userId)
                            .and(scrap.type.eq(ScrapType.POST))
                            .and(scrap.targetId.in(needDbLookup))
                            .and(scrap.isActive.eq(true)))
                    .fetch();

            Set<Long> scrappedPostIds = scraps.stream()
                    .map(l -> l.getTargetId())
                    .collect(Collectors.toSet());

            for (Long postId : needDbLookup) {
                result.put(postId, scrappedPostIds.contains(postId));
            }
        }

        return result;
    }

    private ThumbnailAndConceptMap fetchThumbnailAndConceptMap(List<Long> postIds) {
        Map<Long, String> thumbnailMap = new HashMap<>();
        Map<Long, AiImageConcept> conceptMap = new HashMap<>();
        QPostImageEntity postImage = QPostImageEntity.postImageEntity;
        QAiImageEntity aiImage = QAiImageEntity.aiImageEntity;

        List<Tuple> aiImages = queryFactory.select(aiImage.postId, aiImage.afterImagePath, aiImage.concept)
                .from(aiImage)
                .where(aiImage.postId.in(postIds))
                .fetch();

        Set<Long> aiPostIds = new HashSet<>();

        for (Tuple tuple : aiImages) {
            Long postId = tuple.get(aiImage.postId);
            thumbnailMap.put(postId, tuple.get(aiImage.afterImagePath));
            conceptMap.put(postId, tuple.get(aiImage.concept));
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
        return new ThumbnailAndConceptMap(thumbnailMap, conceptMap);
    }

    private List<PostAllResponseDto.Posts> convertToPostDtos(List<PostEntity> postEntities, int size,
                                                             Map<Long, Boolean> likedMap,
                                                             Map<Long, Boolean> scrappedMap,
                                                             Map<Long, String> thumbnailMap,
                                                             Map<Long, AiImageConcept> conceptMap) {
        return postEntities.stream()
                .limit(size)
                .map(p -> postDtoMapper.toDto(
                        p,
                        conceptMap.getOrDefault(p.getId(), null),
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
