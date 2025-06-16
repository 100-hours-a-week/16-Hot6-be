package com.kakaotech.ott.ott.product.application.serviceImpl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kakaotech.ott.ott.product.presentation.dto.response.ProductGetResponseDto;
import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import com.kakaotech.ott.ott.scrap.domain.repository.ScrapRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kakaotech.ott.ott.aiImage.application.serviceImpl.S3Uploader;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.product.application.service.ProductService;
import com.kakaotech.ott.ott.product.domain.model.Product;
import com.kakaotech.ott.ott.product.domain.model.ProductImage;
import com.kakaotech.ott.ott.product.domain.model.ProductPromotion;
import com.kakaotech.ott.ott.product.domain.model.ProductVariant;
import com.kakaotech.ott.ott.product.domain.repository.ProductImageRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductPromotionRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductVariantRepository;
import com.kakaotech.ott.ott.product.presentation.dto.request.ProductCreateRequestDto;
import com.kakaotech.ott.ott.product.presentation.dto.request.PromotionDto;
import com.kakaotech.ott.ott.product.presentation.dto.request.VariantDto;
import com.kakaotech.ott.ott.product.presentation.dto.response.ProductCreateResponseDto;
import com.kakaotech.ott.ott.user.domain.model.Role;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserAuthRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final UserAuthRepository userAuthRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;
    private final ProductPromotionRepository promotionRepository;
    private final ScrapRepository scrapRepository;
    private final S3Uploader s3Uploader;

    @Value("${cloud.aws.s3.base-url}")
    private String baseUrl;
    private static final int MAX_IMAGE_COUNT = 5;

    @Override
    @Transactional
    public ProductCreateResponseDto createProduct(ProductCreateRequestDto requestDto, Long userId) throws IOException {
        User user = userAuthRepository.findById(userId);
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new CustomException(ErrorCode.USER_FORBIDDEN);
        }

        // 1. 상품 모델 생성
        Product product = Product.createProduct(
            requestDto.getProduct().getType(),
            requestDto.getProduct().getName(),
            requestDto.getProduct().getDescription(),
            requestDto.getProduct().getSpecification()
        );
        
        // 2. 품목 및 이미지 추가
        List<VariantDto> variantDtos = requestDto.getVariants();
        Map<Integer, List<MultipartFile>> variantImagesMap = requestDto.getVariantImagesMap();
        for (int i = 0; i < variantDtos.size(); i++) {
            VariantDto variantDto = variantDtos.get(i);
            List<MultipartFile> variantImages = variantImagesMap.get(i);

            ProductVariant variant = ProductVariant.createVariant(
                product.getId(),
                variantDto.getName(),
                variantDto.getPrice(),
                variantDto.getAvailableQuantity()
            );
            product.addVariant(variant);

            // 해당 Variant의 이미지들 업로드 및 추가
            if (variantImages != null && !variantImages.isEmpty()) {
                addVariantImages(variant, variantImages);
            }

            // 3. 특가 정보가 있다면 추가
            addVariantPromotions(variant, variantDto);
        }
        
        // 4. 저장
        Product savedProduct = productRepository.save(product);

        return new ProductCreateResponseDto(savedProduct.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductGetResponseDto getProduct(Long productId, Long userId) {
        // 상품 조회
        Product product = productRepository.findById(productId);

        boolean scraped = (userId != null) && scrapRepository.existsByUserIdAndTypeAndPostId(userId, ScrapType.SERVICE_PRODUCT, productId);

        ProductGetResponseDto productGetResponseDto = convertToProductGetResponse(product, scraped);

        return productGetResponseDto;
    }
//
//    @Override
//    public ProductListResponseDto getProductList(String type, Long cursorId, int size) {
//        Slice<ServiceProduct> products;
//
//        if (type != null && !type.isEmpty()) {
//            ProductType productType = ProductType.valueOf(type.toUpperCase());
//            products = productRepository.findAllByType(productType, cursorId, size);
//        } else {
//            // 전체 조회 로직 필요
//            products = productRepository.findAllByStatus("ACTIVE", cursorId, size);
//        }
//
//        return ProductListResponseDto.from(products);
//    }

    // === Private Methods ===

    // Variant 이미지 추가 메서드
    private void addVariantImages(ProductVariant variant, List<MultipartFile> variantImages) throws IOException {
        int sequence = 1;
        for (MultipartFile imageFile : variantImages) {
            String imageUrl = baseUrl + s3Uploader.upload(imageFile);
            ProductImage image = ProductImage.createImage(variant.getId(), sequence++, imageUrl);
            variant.addImage(image);
        }
    }

    private void addVariantPromotions(ProductVariant variant, VariantDto variantDto) {
        if (variantDto.getPromotions() != null && !variantDto.getPromotions().isEmpty()) {
            for (PromotionDto promotionDto : variantDto.getPromotions()) {
                if (promotionDto.getDiscountPrice() > variantDto.getPrice()) {
                    throw new CustomException(ErrorCode.INVALID_DISCOUNT);
                }
                ProductPromotion promotion = ProductPromotion.createPromotion(
                        variant.getId(),
                        promotionDto.getType(),
                        promotionDto.getName(),
                        variantDto.getPrice(),
                        promotionDto.getDiscountPrice(),
                        promotionDto.getPromotionQuantity(),
                        promotionDto.getStartAt(),
                        promotionDto.getEndAt(),
                        promotionDto.getMaxPerCustomer()
                );
                variant.addPromotion(promotion);
            }
            variant.setPromotionStatus(true);
        }
    }

    private ProductGetResponseDto convertToProductGetResponse(Product product, boolean scraped) {
        return ProductGetResponseDto.builder()
                .productId(product.getId())
                .productType(product.getType())
                .productName(product.getName())
                .description(product.getDescription())
                .specification(product.getSpecification())
                .variants(convertVariants(product.getVariants()))
                .scraped(scraped)  // 스크랩 여부 설정
                .build();
    }

    private List<String> convertImageUrls(List<ProductImage> images) {
        if (images == null || images.isEmpty()) {
            return List.of(); // 빈 리스트 반환
        }

        return images.stream()
                .sorted((img1, img2) -> Integer.compare(img1.getSequence(), img2.getSequence())) // 시퀀스 순 정렬
                .map(ProductImage::getImageUuid) // UUID 추출
                .collect(Collectors.toList());
    }

    private List<ProductGetResponseDto.VariantResponse> convertVariants(List<ProductVariant> variants) {
        return variants.stream()
                .filter(ProductVariant::isActive)
                .map(this::convertVariant)
                .collect(Collectors.toList());
    }

    private ProductGetResponseDto.VariantResponse convertVariant(ProductVariant variant) {
        return ProductGetResponseDto.VariantResponse.builder()
                .variantId(variant.getId())
                .status(variant.getStatus())
                .name(variant.getName())
                .price(variant.getPrice())
                .imageUrls(convertImageUrls(variant.getImages()))
                .availableQuantity(variant.getAvailableQuantity())
                .reservedQuantity(variant.getReservedQuantity())
                .promotions(convertPromotions(variant.getPromotions()))
                .build();
    }

    private List<ProductGetResponseDto.PromotionResponse> convertPromotions(List<ProductPromotion> promotions) {
        return promotions.stream()
                .filter(ProductPromotion::isActive) // 활성 프로모션만 필터링
                .map(this::convertPromotion)
                .collect(Collectors.toList());
    }

    private ProductGetResponseDto.PromotionResponse convertPromotion(ProductPromotion promotion) {
        return ProductGetResponseDto.PromotionResponse.builder()
                .promotionId(promotion.getId())
                .status(promotion.getStatus())
                .type(promotion.getType())
                .name(promotion.getName())
                .discountPrice(promotion.getDiscountPrice())
                .rate(promotion.getRate())
                .promotionQuantity(promotion.getPromotionQuantity())
                .startAt(promotion.getStartAt())
                .endAt(promotion.getEndAt())
                .maxPerCustomer(promotion.getMaxPerCustomer())
                .build();
    }
}