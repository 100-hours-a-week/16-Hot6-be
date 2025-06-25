package com.kakaotech.ott.ott.aiImage.application.serviceImpl;

import com.kakaotech.ott.ott.aiImage.application.service.FastApiClient;
import com.kakaotech.ott.ott.aiImage.application.service.ImageUploader;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImageConcept;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImageState;
import com.kakaotech.ott.ott.aiImage.domain.model.ImageGenerationHistory;
import com.kakaotech.ott.ott.aiImage.domain.repository.ImageGenerationHistoryRepository;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.recommendProduct.domain.model.AiImageRecommendedProduct;
import com.kakaotech.ott.ott.recommendProduct.domain.model.DeskProduct;
import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageRepository;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.AiImageRecommendedProductRepository;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.DeskProductRepository;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.AiImageAndProductRequestDto;

import com.kakaotech.ott.ott.aiImage.application.service.AiImageService;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.FastApiRequestDto;
import com.kakaotech.ott.ott.aiImage.presentation.dto.response.*;
import com.kakaotech.ott.ott.recommendProduct.presentation.dto.response.ProductResponseDto;
import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import com.kakaotech.ott.ott.scrap.domain.repository.ScrapRepository;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiImageServiceImpl implements AiImageService {

    private final AiImageRepository aiImageRepository;
    private final UserAuthRepository userAuthRepository;

    private final DeskProductRepository deskProductRepository;
    private final ScrapRepository scrapRepository;

    private final ImageUploader imageUploader;
    private final FastApiClient fastApiClient;
    private final ImageGenerationHistoryRepository imageGenerationHistoryRepository;
    private final AiImageRecommendedProductRepository aiImageRecommendedProductRepository;

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

        AiImageResponseDto aiImageResponseDto = new AiImageResponseDto(aiImage.getId(), aiImage.getPostId(), aiImage.getState(), aiImage.getBeforeImagePath(), aiImage.getAfterImagePath(), aiImage.getCreatedAt());

        if(aiImage.getState().equals(AiImageState.PENDING)) {
            return new AiImageAndProductResponseDto(aiImageResponseDto, null);
        } else if(aiImage.getState().equals(AiImageState.FAILED))
            throw new CustomException(ErrorCode.FAILED_GENERATING_IMAGE);

        aiImageResponseDto.updateAfterImagePath(aiImage.getAfterImagePath());

        List<AiImageRecommendedProduct> aiImageRecommendedProducts = aiImageRecommendedProductRepository.findByAiImageId(aiImage.getId());

        if(aiImageRecommendedProducts.isEmpty()) {
            throw new CustomException(ErrorCode.DESK_PRODUCT_NOT_FOUND);
        }


        List<ProductResponseDto> productResponseDtos = aiImageRecommendedProducts.stream()
                .map(recommendedProduct -> {

                    DeskProduct deskProduct = deskProductRepository.findById(recommendedProduct.getDeskProductId());
                    boolean isScrapped = scrapRepository.existsByUserIdAndTypeAndPostId(userId, ScrapType.PRODUCT, deskProduct.getId());


                    return new ProductResponseDto(
                            deskProduct.getId(),
                            deskProduct.getName(),
                            deskProduct.getImagePath(),
                            deskProduct.getPrice(),
                            deskProduct.getPurchaseUrl(),
                            isScrapped,
                            recommendedProduct.getCenterX(),
                            recommendedProduct.getCenterY(),
                            deskProduct.getWeight()
                    );
                })
                .toList();

        return new AiImageAndProductResponseDto(aiImageResponseDto, productResponseDtos);
    }


}
