package com.kakaotech.ott.ott.scrap.application.serviceImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.post.domain.model.Post;
import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
import com.kakaotech.ott.ott.scrap.application.service.ScrapService;
import com.kakaotech.ott.ott.scrap.domain.model.Scrap;
import com.kakaotech.ott.ott.scrap.domain.repository.ScrapRepository;
import com.kakaotech.ott.ott.scrap.presentation.dto.request.ScrapRequestDto;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScrapServiceImpl implements ScrapService {

    private final ScrapRepository scrapRepository;
    private final UserAuthRepository userAuthRepository;
    private final PostRepository postRepository;

    @Transactional
    @Override
    public void likeScrap(Long userId, ScrapRequestDto scrapRequestDto) {

        User user = userAuthRepository.findById(userId);
        Post post = postRepository.findById(scrapRequestDto.getTargetId());

        boolean exists = scrapRepository.existsByUserIdAndTypeAndPostId(userId, scrapRequestDto.getType(), scrapRequestDto.getTargetId());

        // 이미 스크랩 상태라면 아무 동작 하지 않음
        if(exists) {
            throw new CustomException(ErrorCode.SCRAP_ALREADY_EXISTS);
        }

        Scrap scrap = Scrap.createScrap(userId, scrapRequestDto.getType(), scrapRequestDto.getTargetId());
        Scrap savedscrap = scrapRepository.save(scrap);

        postRepository.incrementScrapCount(scrapRequestDto.getTargetId(), 1L);
    }

    @Transactional
    @Override
    public void unlikeScrap(Long userId, ScrapRequestDto scrapRequestDto) {

        User user = userAuthRepository.findById(userId);

        boolean exists = scrapRepository.existsByUserIdAndTypeAndPostId(userId, scrapRequestDto.getType(), scrapRequestDto.getTargetId());

        // 이미 스크랩 상태라면 아무 동작 하지 않음
        if(!exists) {
            throw new CustomException(ErrorCode.SCRAP_NOT_FOUND);
        }

        scrapRepository.deleteByUserEntityIdAndTypeAndTargetId(userId, scrapRequestDto.getTargetId());

        postRepository.incrementScrapCount(scrapRequestDto.getTargetId(), -1L);
    }


}
