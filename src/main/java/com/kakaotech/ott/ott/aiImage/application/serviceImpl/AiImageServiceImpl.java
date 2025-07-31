package com.kakaotech.ott.ott.aiImage.application.serviceImpl;

import com.kakaotech.ott.ott.aiImage.application.service.FastApiClient;
import com.kakaotech.ott.ott.aiImage.application.service.ImageUploader;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImageConcept;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImageState;
import com.kakaotech.ott.ott.aiImage.domain.model.ImageGenerationHistory;
import com.kakaotech.ott.ott.aiImage.domain.repository.ImageGenerationHistoryRepository;
import com.kakaotech.ott.ott.aiImage.infrastructure.stream.AiImageEventPublisher;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.FastApiRequestDto;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageRepository;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.AiImageRecommendedProductRepository;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.AiImageAndProductRequestDto;

import com.kakaotech.ott.ott.aiImage.application.service.AiImageService;
import com.kakaotech.ott.ott.aiImage.presentation.dto.response.*;
import com.kakaotech.ott.ott.recommendProduct.presentation.dto.response.ProductResponseDto;
import com.kakaotech.ott.ott.recommendProduct.presentation.dto.response.RecommendedProductProjection;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserAuthRepository;
import com.kakaotech.ott.ott.util.KstDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiImageServiceImpl implements AiImageService {

    private final AiImageRepository aiImageRepository;
    private final UserAuthRepository userAuthRepository;

    private final ImageUploader imageUploader;
    private final ImageGenerationHistoryRepository imageGenerationHistoryRepository;
    private final AiImageRecommendedProductRepository aiImageRecommendedProductRepository;
    private final FastApiClient fastApiClient;
    private final AiImageEventPublisher aiImageEventPublisher;

    @Override
    @Transactional
    public void checkQuota(Long userId) {

        User user = userAuthRepository.findById(userId);

        int remainQuota = aiImageRepository.countUserIdAndStateIn(userId);

        if (remainQuota == 5) {
            throw new CustomException(ErrorCode.QUOTA_ALREADY_USED);
        }
    }

    @Override
    @Transactional
    public AiImageSaveResponseDto handleImageValidation(MultipartFile image, AiImageConcept concept, Long userId) throws IOException {
        // 1. 이미지 업로드 (S3에 저장 후 퍼블릭 URL 반환)
        String imageUrl = imageUploader.upload(image);

        // 2. FastAPI로 전송하여 이미지 유효성 검사
        FastApiRequestDto request = new FastApiRequestDto(imageUrl, concept);
        FastApiResponseDto response = fastApiClient.sendBeforeImageToFastApi(request);

        System.out.println("fastApi 결과 : " + response.isClassify());

        // 3. 유효하지 않은 경우 이미지 삭제
        if (!response.isClassify()) {
            imageUploader.delete(imageUrl);
            throw new CustomException(ErrorCode.INVALID_IMAGE);
        }

        AiImage aiImage = AiImage.createAiImage(userId, concept, response.getInitialImageUrl());
        AiImage savedAiImage = aiImageRepository.saveImage(aiImage);

        // 4. Redis Stream으로 AI 처리 요청 발행
        try {
            String recordId = aiImageEventPublisher.publishImageProcessingRequest(
                    request.getInitialImageUrl(), request.getConcept());
        } catch (Exception e) {
            log.error("AI 이미지 처리 요청 발행 실패: aiImageId={}", savedAiImage.getId(), e);
        }

        return new AiImageSaveResponseDto(savedAiImage.getId());
    }

    @Override
    @Transactional
    public AiImage insertAiImage(AiImageAndProductRequestDto aiImageAndProductRequestDto) {

        AiImage aiImage = aiImageRepository.findByBeforeImagePath(aiImageAndProductRequestDto.getInitialImageUrl());

        if(aiImageAndProductRequestDto.getProcessedImageUrl() == null) {
            aiImage.failedState();
            return aiImageRepository.saveImage(aiImage);
        }

        aiImage.updateAiImage(aiImageAndProductRequestDto.getProcessedImageUrl());
        aiImage.successState();

        User user = userAuthRepository.findById(aiImage.getUserId());

        ImageGenerationHistory imageGenerationHistory = ImageGenerationHistory.createImageGenerationHisotry(user.getId(), LocalDate.now(), aiImage.getConcept());
        imageGenerationHistoryRepository.save(imageGenerationHistory, user);

        AiImage savedAiImage = aiImageRepository.saveImage(aiImage);

        userAuthRepository.save(user);

        return savedAiImage;
    }

    @Override
    @Transactional(readOnly = true)
    public AiImageAndProductResponseDto getAiImage(Long imageId, Long userId) {
        AiImage aiImage = aiImageRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.AIIMAGE_NOT_FOUND))
                .toDomain();

        if (!aiImage.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.USER_FORBIDDEN);
        }

        AiImageResponseDto aiImageResponseDto = new AiImageResponseDto(
                aiImage.getId(),
                aiImage.getPostId(),
                aiImage.getState(),
                aiImage.getBeforeImagePath(),
                aiImage.getAfterImagePath(),
                new KstDateTime(aiImage.getCreatedAt())
        );

        if (aiImage.getState() == AiImageState.PENDING) {
            return new AiImageAndProductResponseDto(aiImageResponseDto, null);
        } else if (aiImage.getState() == AiImageState.FAILED) {
            throw new CustomException(ErrorCode.FAILED_GENERATING_IMAGE);
        }

        List<RecommendedProductProjection> projections =
                aiImageRecommendedProductRepository.findWithProductAndScrap(aiImage.getId(), userId);

        if (projections.isEmpty()) {
            throw new CustomException(ErrorCode.DESK_PRODUCT_NOT_FOUND);
        }

        List<ProductResponseDto> productResponseDtos = projections.stream()
                .map(p -> new ProductResponseDto(
                        p.getProductId(),
                        p.getProductName(),
                        p.getImagePath(),
                        p.getPrice(),
                        p.getPurchaseUrl(),
                        p.getIsScrapped(),
                        p.getCenterX(),
                        p.getCenterY(),
                        p.getWeight()
                )).toList();

        return new AiImageAndProductResponseDto(aiImageResponseDto, productResponseDtos);
    }


}
