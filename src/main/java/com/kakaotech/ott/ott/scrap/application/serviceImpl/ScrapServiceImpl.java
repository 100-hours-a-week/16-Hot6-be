package com.kakaotech.ott.ott.scrap.application.serviceImpl;

import com.kakaotech.ott.ott.post.domain.model.Post;
import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
import com.kakaotech.ott.ott.scrap.application.service.ScrapService;
import com.kakaotech.ott.ott.scrap.domain.model.Scrap;
import com.kakaotech.ott.ott.scrap.domain.repository.ScrapRepository;
import com.kakaotech.ott.ott.scrap.presentation.dto.request.ScrapRequestDto;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserAuthRepository;
import jakarta.persistence.EntityNotFoundException;
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

        User user = userAuthRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."))
                .toDomain();
        Post post = postRepository.findById(scrapRequestDto.getTargetId());

        boolean exists = scrapRepository.existsByUserIdAndTypeAndPostId(userId, scrapRequestDto.getType(), scrapRequestDto.getTargetId());

        // 이미 좋아요 상태라면 아무 동작 하지 않음
        if(exists)
            return;

        Scrap scrap = Scrap.createScrap(userId, scrapRequestDto.getType(), scrapRequestDto.getTargetId());
        Scrap savedscrap = scrapRepository.save(scrap);

        postRepository.incrementScrapCount(scrapRequestDto.getTargetId(), 1L);
    }

    @Transactional
    @Override
    public void unlikeScrap(Long userId, ScrapRequestDto scrapRequestDto) {

        User user = userAuthRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."))
                .toDomain();

        boolean exists = scrapRepository.existsByUserIdAndTypeAndPostId(userId, scrapRequestDto.getType(), scrapRequestDto.getTargetId());

        // 이미 좋아요 상태라면 아무 동작 하지 않음
        if(!exists)
            return;

        scrapRepository.deleteByUserEntityIdAndTypeAndTargetId(userId, scrapRequestDto.getTargetId());

        postRepository.incrementScrapCount(scrapRequestDto.getTargetId(), -1L);
    }


}
