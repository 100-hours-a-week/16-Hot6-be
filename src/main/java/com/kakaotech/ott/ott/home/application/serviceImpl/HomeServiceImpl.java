package com.kakaotech.ott.ott.home.application.serviceImpl;

import com.kakaotech.ott.ott.global.cache.DistributedLock;
import com.kakaotech.ott.ott.global.config.RedisConfig;
import com.kakaotech.ott.ott.home.application.service.HomeService;
import com.kakaotech.ott.ott.home.presentation.dto.response.MainResponseDto;
import com.kakaotech.ott.ott.post.application.service.PostService;
import com.kakaotech.ott.ott.product.application.service.ProductService;
import com.kakaotech.ott.ott.recommendProduct.application.service.ProductDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {
    private final PostService postService;
    private final ProductDomainService productDomainService;
    private final ProductService productService;

    // 로그인/비로그인 상태에 따라 다른 캐시
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = RedisConfig.MAIN_CACHE, key = "#userId == null ? 'GUEST' : #userId")
    public MainResponseDto getMainPageData(Long userId) {
        return getMainPageDataWithLock(userId);
    }

    @DistributedLock(keyPrefix = "main_page", key = "#userId")
    private MainResponseDto getMainPageDataWithLock(Long userId) {
        return new MainResponseDto(
                postService.getPopularSetups(userId),
                productDomainService.getRecommendItems(userId),
                productService.getTodayPromotionProducts(userId)
        );
    }
}
