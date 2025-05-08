package com.kakaotech.ott.ott.reply.domain.repository;

import com.kakaotech.ott.ott.reply.domain.model.Reply;

import java.util.List;


public interface ReplyRepository {

    List<Reply> findByCommentIdCursor(Long commentId, Long lastCommentId, int size);

    Reply save(Reply reply);

    void delete(Long replyId);

    Reply findById(Long replyId);
}
