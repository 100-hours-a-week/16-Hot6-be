package com.kakaotech.ott.ott.aiImage.application.serviceImpl;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.domain.model.DeskProduct;
import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageRepository;
import com.kakaotech.ott.ott.aiImage.domain.repository.DeskProductRepository;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.DeskProductEntity;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.AiImageAndProductRequestDto;

import com.kakaotech.ott.ott.aiImage.application.service.AiImageService;
import com.kakaotech.ott.ott.aiImage.presentation.dto.response.AiImageAndProductResponseDto;
import com.kakaotech.ott.ott.aiImage.presentation.dto.response.AiImageResponseDto;
import com.kakaotech.ott.ott.aiImage.presentation.dto.response.ProductResponseDto;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiImageServiceImpl implements AiImageService {

    private final AiImageRepository aiImageRepository;
    private final UserRepository userRepository;

    private final DeskProductRepository deskProductRepository;

    @Override
    public AiImage createdAiImage(AiImageAndProductRequestDto aiImageAndProductRequestDto, Long userId) {

        AiImage aiImage = AiImage.createAiImage(userId, aiImageAndProductRequestDto.getInitialImageUrl(), aiImageAndProductRequestDto.getProcessedImageUrl());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."))
                .toDomain();

        user.renewGeneratedDate();
        User generatedUser = userRepository.save(user);

        return aiImageRepository.save(aiImage, user).toDomain();
    }

    @Override
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

        AiImageResponseDto aiImageResponseDto = new AiImageResponseDto(aiImage.getId(), aiImage.getAfterImagePath(), aiImage.getCreatedAt());

        return new AiImageAndProductResponseDto(aiImageResponseDto, productResponseDtos, false, null);
    }


}
