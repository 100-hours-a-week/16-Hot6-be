package com.kakaotech.ott.ott.admin.application;

import com.kakaotech.ott.ott.admin.presentation.dto.request.PromotionCreateRequestDto;
import com.kakaotech.ott.ott.admin.presentation.dto.response.AdminDeliveryResponseDto;
import com.kakaotech.ott.ott.admin.presentation.dto.response.AdminProductStatusResponseDto;
import com.kakaotech.ott.ott.admin.presentation.dto.response.AdminRefundResponseDto;
import com.kakaotech.ott.ott.admin.presentation.dto.response.PromotionCreateResponseDto;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;
import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemRepository;
import com.kakaotech.ott.ott.payment.domain.model.Payment;
import com.kakaotech.ott.ott.payment.domain.repository.PaymentRepository;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointActionReason;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointActionType;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointHistory;
import com.kakaotech.ott.ott.pointHistory.domain.repository.PointHistoryRepository;
import com.kakaotech.ott.ott.product.domain.model.Product;
import com.kakaotech.ott.ott.product.domain.model.ProductPromotion;
import com.kakaotech.ott.ott.product.domain.model.ProductVariant;
import com.kakaotech.ott.ott.product.domain.repository.ProductPromotionRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductVariantRepository;
import com.kakaotech.ott.ott.product.presentation.dto.request.PromotionDto;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderRepository;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import com.kakaotech.ott.ott.util.KstDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductOrderRepository productOrderRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductPromotionRepository productPromotionRepository;
    private final PaymentRepository paymentRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional(readOnly = true)
    @Override
    public AdminProductStatusResponseDto getOrderProductStatus() {

        List<OrderItem> refundedProduct = orderItemRepository.findByStatus(OrderItemStatus.REFUND_REQUEST);
        List<OrderItem> paidProduct = orderItemRepository.findByStatus(OrderItemStatus.PAID);

        // 1. RefundedOrderItems 생성
        List<AdminProductStatusResponseDto.RefundedOrderItems> refundedOrderItems = refundedProduct.stream()
                .map(item -> {
                    Long ordererId = productOrderRepository.findById(item.getOrderId()).getUserId();
                    User orderer = userRepository.findById(ordererId);
                    ProductVariant productVariant = productVariantRepository.findById(item.getVariantsId());

                    return new AdminProductStatusResponseDto.RefundedOrderItems(
                            item.getId(),
                            item.getPromotionId(),
                            orderer.getNicknameKakao(),
                            productVariant.getName(),
                            item.getQuantity(),
                            item.getFinalPrice(),
                            item.getRefundReason(),
                            new KstDateTime(item.getRefundedAt())
                    );
                })
                .toList();

        // 2. PaidProductOrder 그룹핑 처리 (ProductOrderId 기준)
        Map<Long, List<OrderItem>> paidGrouped = paidProduct.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        List<AdminProductStatusResponseDto.PaidProductOrder> paidProductOrders = paidGrouped.entrySet().stream()
                .map(entry -> {
                    Long productOrderId = entry.getKey();
                    List<AdminProductStatusResponseDto.PaidProductOrder.PaidOrderItem> paidOrderItems = entry.getValue().stream()
                            .map(item -> {
                                Long ordererId = productOrderRepository.findById(item.getOrderId()).getUserId();
                                User orderer = userRepository.findById(ordererId);
                                ProductVariant productVariant = productVariantRepository.findById(item.getVariantsId());

                                return new AdminProductStatusResponseDto.PaidProductOrder.PaidOrderItem(
                                        item.getId(),
                                        item.getPromotionId(),
                                        orderer.getNicknameKakao(),
                                        productVariant.getName(),
                                        item.getQuantity(),
                                        item.getFinalPrice(),
                                        new KstDateTime(productVariant.getCreatedAt())
                                );
                            })
                            .toList();

                    return new AdminProductStatusResponseDto.PaidProductOrder(productOrderId, paidOrderItems);
                })
                .toList();

        return new AdminProductStatusResponseDto(paidProductOrders, refundedOrderItems);
    }

    @Transactional
    @Override
    public AdminDeliveryResponseDto deliveryProduct(Long productOrderId) {

        List<OrderItem> orderItem = orderItemRepository.findByProductOrderId(productOrderId);

        for (OrderItem item : orderItem) {
            item.deliver();
            orderItemRepository.deliveryOrderItem(item);
        }

        ProductOrder productOrder = productOrderRepository.findById(productOrderId);
        productOrder.deliver();
        productOrderRepository.deliveryProductOrder(productOrder);

        return new AdminDeliveryResponseDto(true);
    }

    @Transactional
    @Override
    public AdminRefundResponseDto refundApproveProduct(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId);
        orderItem.refundApprove();
        orderItemRepository.refundOrderItem(orderItem);

        ProductOrder productOrder = productOrderRepository.findById(orderItem.getOrderId());
        User user = userRepository.findById(productOrder.getUserId());
        Payment payment = paymentRepository.findByProductOrderId(productOrder.getId());
        PointHistory lastPointHistory = pointHistoryRepository.findLatestPointHistoryByUserId(productOrder.getUserId());
        PointHistory pointHistory = PointHistory.createPointHistory(productOrder.getUserId(), orderItem.getRefundAmount(), lastPointHistory.getBalanceAfter() + orderItem.getRefundAmount(),
                PointActionType.EARN, PointActionReason.PRODUCT_REFUND);

        int remainNotRefundedProduct = orderItemRepository.countByProductOrderIdAndStatusNot(productOrder.getId(), OrderItemStatus.REFUND_APPROVED);

        if (remainNotRefundedProduct > 0) {
            productOrder.partialRefund();
            payment.partialRefund(orderItem.getRefundAmount(), orderItem.getRefundedAt());
        } else {
            productOrder.refund();
            payment.refund(orderItem.getRefundAmount(), orderItem.getRefundedAt());
        }

        productOrderRepository.refundProductOrder(productOrder);
        paymentRepository.refund(payment);
        pointHistoryRepository.save(pointHistory, user);

        return new AdminRefundResponseDto(true);
    }

    @Transactional
    @Override
    public AdminRefundResponseDto refundRejectProduct(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId);
        orderItem.refundReject();
        orderItemRepository.refundOrderItem(orderItem);

        return new AdminRefundResponseDto(true);
    }

    @Transactional
    @Override
    public PromotionCreateResponseDto createPromotion(Long variantId, PromotionCreateRequestDto promotionCreateRequestDto) {
        ProductVariant variant = productVariantRepository.findById(variantId);

        PromotionDto promotionDto = promotionCreateRequestDto.getPromotion();
        // 요청 데이터 검증
        validatePromotionRequest(variant, promotionDto);

        // 진행 중인 특가 확인
        checkActivePromotion(variantId);

        // 특가 정보 생성
        ProductPromotion promotion = ProductPromotion.createPromotion(
                variantId,
                promotionDto.getType(),
                promotionDto.getName(),
                variant.getPrice(),
                promotionDto.getDiscountPrice(),
                promotionDto.getTotalQuantity(),
                promotionDto.getStartAt(),
                promotionDto.getEndAt(),
                promotionDto.getMaxPerCustomer()
        );


        ProductPromotion savedPromotion = productPromotionRepository.save(promotion);

        // Variant 재고 예약 처리
        variant.setPromotionStatus(true);
        variant.reserveStock(promotionDto.getTotalQuantity());
        productVariantRepository.update(variant);

        return new PromotionCreateResponseDto(savedPromotion.getId());
    }

    // === private Method ===

    // 특가 생성 요청 검증
    private void validatePromotionRequest(ProductVariant variant, PromotionDto request) {
        // 1. 할인 가격 검증
        if (request.getDiscountPrice() >= variant.getPrice()) {
            throw new CustomException(ErrorCode.INVALID_DISCOUNT);
        }

        // 2. 재고 수량 검증
        if (request.getTotalQuantity() > variant.getAvailableQuantity()) {
            throw new CustomException(ErrorCode.INSUFFICIENT_STOCK);
        }

        // 3. 시작/종료 시간 검증
//        LocalDateTime now = LocalDateTime.now();

        if (!request.getStartAt().isBefore(request.getEndAt())) {
            throw new CustomException(ErrorCode.INVALID_DATE_RANGE);
        }

//        if (!request.getEndAt().isAfter(now)) {
//            throw new BusinessException(ErrorCode.INVALID_END_DATE,
//                    "종료시간은 현재시간 이후여야 합니다.");
//        }
    }

    // 진행 중인 특가 확인
    private void checkActivePromotion(Long variantId) {
        LocalDateTime now = LocalDateTime.now();

//        Optional<ProductPromotion> activePromotion = productPromotionRepository
//                .findCurrentPromotion(variantId, now);

        productPromotionRepository.findCurrentPromotion(variantId, now)
                .ifPresent(promo -> {
                    throw new CustomException(ErrorCode.PROMOTION_ALREADY_EXISTS);
                });
    }
}
