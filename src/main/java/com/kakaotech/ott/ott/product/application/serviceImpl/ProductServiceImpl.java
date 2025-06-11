package com.kakaotech.ott.ott.product.application.serviceImpl;

import java.io.IOException;

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
        
        // 2. 품목 추가
        for (VariantDto variantDto : requestDto.getVariants()) {
            ProductVariant variant = ProductVariant.createVariant(
                product.getId(),
                variantDto.getName(),
                variantDto.getPrice(),
                variantDto.getAvailableQuantity()
            );
            product.addVariant(variant);


            // 3. 특가 정보가 있다면 추가
            if (variantDto.getPromotions() != null) {
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
            }
        }
        
        // 4. 이미지 처리
        if (requestDto.getImages() != null) {
            int seq = 1;
            for (MultipartFile file : requestDto.getImages()) {
                String url = baseUrl + s3Uploader.upload(file);
                product.addImage(ProductImage.createImage(product.getId(), seq++, url));
            }
        }
        
        // 5. 저장
        Product savedProduct = productRepository.save(product);

        return new ProductCreateResponseDto(savedProduct.getId());
    }

//    @Override
//    public ProductDetailResponseDto getProductDetail(Long productId) {
//        ServiceProduct product = productRepository.findById(productId)
//                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
//
//        return ProductDetailResponseDto.from(product);
//    }
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
}