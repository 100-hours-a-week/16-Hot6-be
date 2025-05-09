package com.kakaotech.ott.ott.aiImage.presentation.controller;

import com.kakaotech.ott.ott.aiImage.application.service.ProductDomainService;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.domain.model.DeskProduct;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.AiImageAndProductRequestDto;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.AiImageUploadRequestDto;
import com.kakaotech.ott.ott.aiImage.presentation.dto.response.*;
import com.kakaotech.ott.ott.aiImage.application.service.AiImageService;
import com.kakaotech.ott.ott.global.response.ApiResponse;

import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import com.kakaotech.ott.ott.user.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class AiImageController {

    private final AiImageService aiImageService;
    private final ProductDomainService productDomainService;
    private final UserService userService;

    @GetMapping("/ai-images/upload")
    public ResponseEntity<ApiResponse<CheckAiImageQuotaResponseDto>> checkAiImageQuota(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Long userId = userPrincipal.getId();

        boolean checkQuota = userService.checkQuota(userId);

        if (!checkQuota)
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(403, "오늘은 더 이상 이미지를 생성할 수 없습니다."));

        CheckAiImageQuotaResponseDto responseDto = new CheckAiImageQuotaResponseDto(1);
        return ResponseEntity.ok(ApiResponse.success("AI 이미지 생성이 가능합니다.", responseDto));

    }

    @PostMapping(value = "/ai-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AiImageSaveResponseDto>> uploadAiImage(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ModelAttribute AiImageUploadRequestDto requestDto) throws IOException {

        Long userId = userPrincipal.getId();

        MultipartFile image = requestDto.getBeforeImagePath();

        if (image == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(400, "이미지 파일이 누락되었습니다."));
        }

        if (image.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(400, "이미지 파일이 비어있습니다."));
        }


        AiImageSaveResponseDto aiImageSaveResponseDto = aiImageService.handleImageValidation(image, userId);

        return ResponseEntity.ok(ApiResponse.success("AI 이미지 생성 요청이 접수되었습니다.", aiImageSaveResponseDto));
    }

    @PostMapping("/ai-images/result")
    public ResponseEntity<ApiResponse<AiImageSaveResponseDto>> receiveResult(
            @Valid @RequestBody AiImageAndProductRequestDto requestDto) {

        AiImage aiImage = aiImageService.insertAiImage(requestDto);

        List<DeskProduct> deskProduct = productDomainService.createdProduct(requestDto, aiImage, aiImage.getUserId());
        AiImageSaveResponseDto aiImageSaveResponseDto = new AiImageSaveResponseDto(deskProduct.getFirst().getAiImageId());

        return ResponseEntity
                .status(HttpStatus.CREATED)  // 또는 .status(201)
                .body(ApiResponse.success("AI 이미지 저장이 완료됐습니다.", aiImageSaveResponseDto));
    }

    @GetMapping("/ai-images/{imageId}")
    public ResponseEntity<ApiResponse<AiImageAndProductResponseDto>> getAiImageAndProduct(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                                          @PathVariable Long imageId) {

        AiImageAndProductResponseDto aiImageAndProductResponseDto = aiImageService.getAiImage(imageId, userPrincipal.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("데스크 이미지 및 추천 제품 조회 성공", aiImageAndProductResponseDto));
    }

}
