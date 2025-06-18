package com.kakaotech.ott.ott.product.application.serviceImpl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kakaotech.ott.ott.product.domain.model.*;
import com.kakaotech.ott.ott.product.presentation.dto.response.ProductGetResponseDto;
import com.kakaotech.ott.ott.product.presentation.dto.response.ProductListResponseDto;
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
                variantDto.getTotalQuantity()
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

    @Override
    @Transactional(readOnly = true)
    public ProductListResponseDto getProductList(Long userId, ProductType productType, PromotionType promotionType,
                                                 Long lastProductId, int size) {
        // 상품 목록 조회
        List<Product> products = fetchProducts(productType, promotionType, lastProductId, size);

        List<ProductListResponseDto.Products> productDtos = products.stream()
                .map(product -> convertToProductListDto(product, userId, promotionType != null))
                .collect(Collectors.toList());

        boolean hasNext = productDtos.size() == size;
        Long nextLastProductId = hasNext ? productDtos.get(productDtos.size() - 1).getProductId() : null;

        ProductListResponseDto.Pagination pagination = new ProductListResponseDto.Pagination(
                size, nextLastProductId, hasNext);

        return ProductListResponseDto.builder()
                .products(productDtos)
                .pagination(pagination)
                .build();
    }

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
                        promotionDto.getTotalQuantity(),
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
                .availableQuantity(promotion.getAvailableQuantity())
                .startAt(promotion.getStartAt())
                .endAt(promotion.getEndAt())
                .maxPerCustomer(promotion.getMaxPerCustomer())
                .build();
    }

    private ProductListResponseDto.Products createRegularProductDto(
            Product product,
            ProductVariant variant,
            String imageUrl,
            boolean scraped) {

        return ProductListResponseDto.Products.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productType(product.getType().toString())
                .variantName(variant.getName())
                .imageUrl(imageUrl)
                .originalPrice(variant.getPrice())
                .discountPrice(null)
                .discountRate(null)
                .availableQuantity(variant.getAvailableQuantity())
                .promotionEndAt(null)
                .promotion(false)
                .scraped(scraped)
                .createdAt(product.getCreatedAt())
                .build();
    }

    // 상품 목록 조회
    private List<Product> fetchProducts(ProductType productType, PromotionType promotionType, Long lastProductId, int size) {
        if (promotionType != null) {
            // 특가 상품 조회 (종료 임박순)
            return productRepository.findPromotionProductsByCursor(promotionType, lastProductId, size);
        } else {
            // 일반 상품 조회 (특가 상품 제외, 최신순)
            return productRepository.findProductsByCursor(productType, lastProductId, size);
        }
    }

    /**
     * 특가 상품용 DTO 생성 팩토리 메서드
     */
    private ProductListResponseDto.Products createPromotionProductDto(
            Product product,
            ProductVariant variant,
            ProductPromotion promotion,
            String imageUrl,
            boolean scraped) {

        return ProductListResponseDto.Products.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productType(product.getType().toString())
                .variantName(variant.getName())
                .imageUrl(imageUrl)
                .originalPrice(promotion.getOriginalPrice())
                .discountPrice(promotion.getDiscountPrice())
                .discountRate(promotion.getRate())
                .availableQuantity(promotion.getAvailableQuantity())
                .promotionEndAt(promotion.getEndAt())
                .promotion(true)
                .scraped(scraped)
                .createdAt(product.getCreatedAt())
                .build();
    }

    private ProductListResponseDto.Products convertToProductListDto(Product product, Long userId, boolean isPromotionProduct) {
        // 공통 데이터 추출
        ProductVariant firstVariant = getFirstActiveVariant(product);
        String imageUrl = getFirstImageUrl(firstVariant);
        boolean scrapped = isProductScrapped(userId, product.getId());

        if (isPromotionProduct) {
            ProductPromotion activePromotion = getActivePromotion(firstVariant);
            return createPromotionProductDto(product, firstVariant, activePromotion, imageUrl, scrapped);
        } else {
            return createRegularProductDto(product, firstVariant, imageUrl, scrapped);
        }
    }

    // == 헬퍼 메서드 ==
    private ProductVariant getFirstActiveVariant(Product product) {
        return product.getVariants().stream()
                .filter(ProductVariant::isActive)
                .min((v1, v2) -> Long.compare(v1.getId(), v2.getId()))
                .orElseThrow(() -> new CustomException(ErrorCode.VARIANT_NOT_FOUND));
    }

    private String getFirstImageUrl(ProductVariant variant) {
        return variant.getImages().stream()
                .filter(img -> img.getSequence() == 1)
                .map(ProductImage::getImageUuid)
                .findFirst()
                .orElse("");
    }

    private boolean isProductScrapped(Long userId, Long productId) {
        return (userId != null) &&
                scrapRepository.existsByUserIdAndTypeAndPostId(userId, ScrapType.SERVICE_PRODUCT, productId);
    }

    private ProductPromotion getActivePromotion(ProductVariant variant) {
        return variant.getPromotions().stream()
                .filter(ProductPromotion::isActive)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.PROMOTION_NOT_FOUND));
    }
}