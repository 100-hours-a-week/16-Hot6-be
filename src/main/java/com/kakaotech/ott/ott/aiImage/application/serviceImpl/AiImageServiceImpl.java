package com.kakaotech.ott.ott.aiImage.application.serviceImpl;

import com.kakaotech.ott.ott.aiImage.application.service.FastApiClient;
import com.kakaotech.ott.ott.aiImage.application.service.ImageUploader;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.domain.model.DeskProduct;
import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageRepository;
import com.kakaotech.ott.ott.aiImage.domain.repository.DeskProductRepository;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.DeskProductEntity;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.AiImageAndProductRequestDto;

import com.kakaotech.ott.ott.aiImage.application.service.AiImageService;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.FastApiRequestDto;
import com.kakaotech.ott.ott.aiImage.presentation.dto.response.*;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiImageServiceImpl implements AiImageService {

    private final AiImageRepository aiImageRepository;
    private final UserRepository userRepository;

    private final DeskProductRepository deskProductRepository;

    private final ImageUploader imageUploader;
    private final FastApiClient fastApiClient;

    @Override
    @Transactional
    public AiImageSaveResponseDto handleImageValidation(MultipartFile image, Long userId) throws IOException {
        // 1. 이미지 업로드 (S3에 저장 후 퍼블릭 URL 반환)
        String imageUrl = imageUploader.upload(image);

        // 2. FastAPI로 전송하여 이미지 유효성 검사
        FastApiRequestDto request = new FastApiRequestDto(imageUrl);
        FastApiResponseDto response = fastApiClient.sendBeforeImageToFastApi(request);

        System.out.println("fastApi 결과 : " + response.isClassify());

        // 3. 유효하지 않은 경우 이미지 삭제
        if (!response.isClassify()) {
            imageUploader.delete(imageUrl);
            throw new IllegalArgumentException("올바르지 않은 데스크 이미지입니다.");
        }

        AiImage aiImage = AiImage.createAiImage(userId, response.getInitialImageUrl());
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

        User user = userRepository.findById(aiImage.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."))
                .toDomain();

        user.renewGeneratedDate();

        AiImage savedAiImage = aiImageRepository.saveImage(aiImage);

        userRepository.save(user);

        return savedAiImage;
    }

    @Override
    @Transactional
    public AiImageAndProductResponseDto getAiImage(Long imageId, Long userId) {
        AiImage aiImage = aiImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("AI 이미지가 존재하지 않습니다."))
                .toDomain();

        List<DeskProductEntity> entities = deskProductRepository.findByAiImageId(imageId);
        if (entities.isEmpty()) {
            throw new EntityNotFoundException("AI 이미지에 대한 상품이 존재하지 않습니다.");
        }

        List<DeskProduct> deskProducts = entities.stream()
                .map(DeskProductEntity::toDomain)
                .toList();

        List<ProductResponseDto> productResponseDtos = deskProducts.stream()
                .map(product -> new ProductResponseDto(product.getId(), product.getName(), product.getImagePath(),
                        product.getPrice(), product.getPurchaseUrl(), true, product.getWeight()))
                .toList();

        AiImageResponseDto aiImageResponseDto = new AiImageResponseDto(aiImage.getId(), aiImage.getState(), aiImage.getAfterImagePath(), aiImage.getCreatedAt());

        return new AiImageAndProductResponseDto(aiImageResponseDto, productResponseDtos, false, null);
    }


}
