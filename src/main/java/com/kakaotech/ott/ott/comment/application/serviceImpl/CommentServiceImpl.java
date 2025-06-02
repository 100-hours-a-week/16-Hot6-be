package com.kakaotech.ott.ott.comment.application.serviceImpl;

import com.kakaotech.ott.ott.comment.application.service.CommentService;
import com.kakaotech.ott.ott.comment.domain.model.Comment;
import com.kakaotech.ott.ott.comment.domain.repository.CommentRepository;
import com.kakaotech.ott.ott.comment.presentation.dto.request.CommentCreateRequestDto;
import com.kakaotech.ott.ott.comment.presentation.dto.response.CommentCreateResponseDto;
import com.kakaotech.ott.ott.comment.presentation.dto.response.CommentListResponseDto;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserAuthRepository userAuthRepository;
    private final PostRepository postRepository;

    @Value("${cloud.aws.s3.basic-profile}")
    private String basicProfile;

    @Override
    @Transactional
    public CommentCreateResponseDto createComment(CommentCreateRequestDto commentCreateRequestDto, Long userId, Long postId) {

        Comment comment = Comment.createComment(userId, postId, commentCreateRequestDto.getContent());
        Comment savedComment = commentRepository.save(comment);

        postRepository.incrementCommentCount(postId, 1L);

        return new CommentCreateResponseDto(savedComment.getId());
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {

        User user = userAuthRepository.findById(userId);

        if(!user.getId().equals(userId))
            throw new CustomException(ErrorCode.USER_FORBIDDEN);

        Comment comment = commentRepository.findById(commentId);

        System.out.println(commentId);
        commentRepository.deleteComment(commentId);

        postRepository.incrementCommentCount(comment.getPostId(), -1L);

    }

    @Override
    @Transactional
    public CommentCreateResponseDto updateComment(CommentCreateRequestDto commentCreateRequestDto, Long commentId, Long userId) {

        userAuthRepository.findById(userId);

        Comment comment = commentRepository.findById(commentId);

        if(!comment.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.USER_FORBIDDEN);
        }

        if (commentCreateRequestDto.getContent() != null) {
            comment.updateContent(commentCreateRequestDto.getContent());
        }

        Comment savedComment = commentRepository.save(comment);
        return new CommentCreateResponseDto(savedComment.getId());

    }

    @Override
    @Transactional(readOnly = true)
    public CommentListResponseDto findByPostIdCursor(Long userId, Long postId, Long lastCommentId, int size) {
        // 1) DB에서 댓글을 조회
        List<Comment> commentList = commentRepository.findByPostIdCursor(postId, lastCommentId, size + 1);

        // 2) hasNext, 실제 반환할 개수, 새로운 lastCommentId 계산
        boolean hasNext = commentList.size() > size;
        if (hasNext) {
            commentList = commentList.subList(0, size);
        }
        Long newLastCommentId = commentList.isEmpty() ? null
                : commentList.get(commentList.size() - 1).getId();

        // 3) 도메인 Comment → DTO 로 변환
        List<CommentListResponseDto.CommentResponseDto> commentDtos =
                commentList.stream()
                        .map(c -> {
                            // 작성자 정보 조회
                            User author = userAuthRepository.findById(c.getUserId());

                            CommentListResponseDto.AuthorDto authorDto =
                                    new CommentListResponseDto.AuthorDto(
                                            author.isActive()
                                                    ? author.getNicknameCommunity()
                                                    : "알 수 없음",
                                            author.isActive()
                                                    ? author.getImagePath()
                                                    : basicProfile
                                    );

                            boolean isOwner = c.getUserId().equals(userId);

                            return new CommentListResponseDto.CommentResponseDto(
                                    c.getId(),
                                    c.getContent(),
                                    authorDto,
                                    c.getCreatedAt(),
                                    isOwner
                            );
                        })
                        .collect(Collectors.toList());

        // 4) 페이지 정보 DTO 생성
        CommentListResponseDto.PageInfo pageInfo =
                new CommentListResponseDto.PageInfo(size, hasNext, newLastCommentId);

        // 5) 최종 래핑하여 반환
        return new CommentListResponseDto(commentDtos, pageInfo);
    }
}