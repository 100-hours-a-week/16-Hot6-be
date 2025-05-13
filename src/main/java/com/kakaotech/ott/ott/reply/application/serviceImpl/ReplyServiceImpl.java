package com.kakaotech.ott.ott.reply.application.serviceImpl;

import com.kakaotech.ott.ott.comment.domain.repository.CommentRepository;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
import com.kakaotech.ott.ott.reply.application.service.ReplyService;
import com.kakaotech.ott.ott.reply.domain.model.Reply;
import com.kakaotech.ott.ott.reply.domain.repository.ReplyRepository;
import com.kakaotech.ott.ott.reply.presentation.dto.request.ReplyCreateRequestDto;
import com.kakaotech.ott.ott.reply.presentation.dto.response.ReplyCreateResponseDto;
import com.kakaotech.ott.ott.reply.presentation.dto.response.ReplyListResponseDto;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserAuthRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserAuthRepository userAuthRepository;

    @Override
    public ReplyListResponseDto getAllReply(Long userId, Long commentId, Long lastReplyId, int size) {

        List<Reply> replyList = replyRepository.findByCommentIdCursor(commentId, lastReplyId, size + 1);

        boolean hasNext = replyList.size() > size;
        if(hasNext) {
            replyList = replyList.subList(0, size);
        }
        Long newLastReplyId = replyList.isEmpty() ? null
                : replyList.get(replyList.size() -1).getId();

        List<ReplyListResponseDto.ReplyResponseDto> replyDtos =
                replyList.stream()
                        .map(r -> {
                            User author = userAuthRepository.findById(r.getUserId());

                            ReplyListResponseDto.AuthorDto authorDto =
                                    new ReplyListResponseDto.AuthorDto(
                                            author.getNicknameCommunity(),
                                            author.getImagePath()
                                    );

                            boolean isOwner = r.getUserId().equals(userId);

                            return new ReplyListResponseDto.ReplyResponseDto(
                                    r.getId(),
                                    r.getContent(),
                                    authorDto,
                                    r.getCreatedAt(),
                                    isOwner
                            );
                        })
                        .collect(Collectors.toList());

        ReplyListResponseDto.PageInfo pageInfo =
                new ReplyListResponseDto.PageInfo(size, hasNext, newLastReplyId);

        return new ReplyListResponseDto(replyDtos, pageInfo);
    }

    @Override
    public ReplyCreateResponseDto createReply(ReplyCreateRequestDto replyCreateRequestDto, Long userId, Long commentId) {

        Reply reply = Reply.createReply(userId, commentId, replyCreateRequestDto.getContent());

        Reply savedRely = replyRepository.save(reply);

        Long postId = commentRepository.findById(savedRely.getCommentId()).getPostId();

        postRepository.incrementCommentCount(postId, 1L);

        return new ReplyCreateResponseDto(savedRely.getId());
    }

    @Override
    public ReplyCreateResponseDto updateReply(ReplyCreateRequestDto replyCreateRequestDto, Long replyId, Long userId) {

        Reply reply = replyRepository.findById(replyId);

        if(!reply.getUserId().equals(userId))
            throw new CustomException(ErrorCode.USER_FORBIDDEN);

        if(replyCreateRequestDto.getContent() != null)
            reply.updateContent(replyCreateRequestDto.getContent());

        Reply savedReply = replyRepository.save(reply);

        return new ReplyCreateResponseDto(savedReply.getId());
    }

    @Override
    public void deleteReply(Long replyId, Long userId){

        userAuthRepository.findById(userId);

        Reply reply = replyRepository.findById(replyId);

        if(!reply.getUserId().equals(userId))
            throw new CustomException(ErrorCode.USER_FORBIDDEN);

        replyRepository.delete(replyId);

        Long postId = commentRepository.findById(reply.getCommentId()).getPostId();

        postRepository.incrementCommentCount(postId, -1L);
    }
}
