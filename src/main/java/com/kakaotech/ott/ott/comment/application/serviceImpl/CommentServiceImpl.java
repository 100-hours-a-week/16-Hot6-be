package com.kakaotech.ott.ott.comment.application.serviceImpl;

import com.kakaotech.ott.ott.comment.application.service.CommentService;
import com.kakaotech.ott.ott.comment.domain.model.Comment;
import com.kakaotech.ott.ott.comment.domain.repository.CommentRepository;
import com.kakaotech.ott.ott.comment.presentation.dto.request.CommentRequestDto;
import com.kakaotech.ott.ott.comment.presentation.dto.response.CommentResponseDto;
import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public CommentResponseDto createComment(CommentRequestDto commentRequestDto, Long userId, Long postId) {

        Comment comment = Comment.createComment(userId, postId, commentRequestDto.getContent());
        Comment savedComment = commentRepository.save(comment);

        postRepository.incrementCommentCount(postId, 1L);

        return new CommentResponseDto(savedComment.getId());
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 아닙니다."))
                .toDomain();

        if(user.getId() != userId)
            throw new AccessDeniedException("댓글 작성자가 아닙니다.");

        Comment comment = commentRepository.findById(commentId);

        commentRepository.deleteComment(commentId);

        postRepository.incrementCommentCount(comment.getPostId(), -1L);
    }

    @Override
    public CommentResponseDto updateComment(CommentRequestDto commentRequestDto, Long commentId, Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 아닙니다."))
                .toDomain();

        Comment comment = commentRepository.findById(commentId);

        if(!comment.getUserId().equals(userId)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        if (commentRequestDto.getContent() != null) {
            comment.updateContent(commentRequestDto.getContent());
        }

        Comment savedComment = commentRepository.save(comment);
        return new CommentResponseDto(savedComment.getId());

    }
}