package com.kakaotech.ott.ott.productOrder.application.serviceimpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.orderItem.domain.model.CancelReason;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;
import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemQueryRepository;
import com.kakaotech.ott.ott.payment.domain.model.Payment;
import com.kakaotech.ott.ott.payment.domain.model.PaymentMethod;
import com.kakaotech.ott.ott.payment.domain.repository.PaymentRepository;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointActionReason;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointActionType;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointHistory;
import com.kakaotech.ott.ott.pointHistory.domain.repository.PointHistoryRepository;
import com.kakaotech.ott.ott.product.domain.model.ProductImage;
import com.kakaotech.ott.ott.product.domain.model.ProductPromotion;
import com.kakaotech.ott.ott.product.domain.model.ProductVariant;
import com.kakaotech.ott.ott.product.domain.model.PromotionStatus;
import com.kakaotech.ott.ott.product.domain.repository.ProductImageRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductPromotionRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductVariantRepository;
import com.kakaotech.ott.ott.productOrder.application.service.ProductOrderService;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderRepository;
import com.kakaotech.ott.ott.productOrder.presentation.dto.request.ProductOrderCancelRequestDto;
import com.kakaotech.ott.ott.productOrder.presentation.dto.request.ProductOrderPartialCancelRequestDto;
import com.kakaotech.ott.ott.productOrder.presentation.dto.request.ProductOrderRequestDto;
import com.kakaotech.ott.ott.productOrder.presentation.dto.response.*;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemRepository;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import com.kakaotech.ott.ott.util.KstDateTime;
import com.kakaotech.ott.ott.util.validator.OrderFingerprintUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductOrderServiceImpl implements ProductOrderService {

    private final ProductOrderRepository productOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;

    private final ProductImageRepository productImageRepository;
    private final ProductPromotionRepository productPromotionRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemQueryRepository orderItemQueryRepository;

    @Override
    @Transactional
    public ProductOrderResponseDto create(ProductOrderRequestDto productOrderRequestDto, Long userId) {

        User user = checkUser(userId);

        String fingerprint = OrderFingerprintUtil.generateFingerprint(productOrderRequestDto.getProducts());
        validateDuplicateOrder(userId, fingerprint);

        List<OrderItem> orderItems = createOrderItems(productOrderRequestDto);
        int totalAmount = calculateTotalAmount(orderItems);
        int discountAmount = calculateTotalDiscount(orderItems);

        ProductOrder order = saveProductOrder(userId, totalAmount, discountAmount, fingerprint, user);
        saveOrderItems(order, orderItems);

        return buildOrderResponse(order, orderItems);
    }

    @Override
    @Transactional(readOnly = true)
    public MyProductOrderHistoryListResponseDto getProductOrderHistory(Long userId, Long lastId, int size) {

        User user = checkUser(userId);

        Slice<ProductOrder> orders = productOrderRepository.findAllByUserId(userId, lastId, size);
        List<ProductOrder> orderContent = orders.getContent();

        if (orderContent.isEmpty()) {
            return MyProductOrderHistoryListResponseDto.builder()
                    .orders(List.of())
                    .pagination(new MyProductOrderHistoryListResponseDto.Pagination(size, null, false))
                    .build();
        }

        List<Long> orderIds = orderContent.stream()
                .map(ProductOrder::getId)
                .toList();

        List<ProductInfoDto> productInfos = orderItemQueryRepository.findAllByOrderIds(orderIds);

        Map<Long, List<ProductInfoDto>> productMap = productInfos.stream()
                .collect(Collectors.groupingBy(ProductInfoDto::getOrderId));

        List<MyProductOrderHistoryResponseDto> dtoList = orderContent.stream()
                .map(order -> {
                    List<ProductInfoDto> products = productMap.getOrDefault(order.getId(), List.of());

                    List<MyProductOrderHistoryResponseDto.ProductDto> productInfoList = products.stream()
                            .map(p -> MyProductOrderHistoryResponseDto.ProductDto.builder()
                                    .productId(p.getProductId())
                                    .status(p.getItemStatus())
                                    .productName(p.getProductName())
                                    .quantity(p.getQuantity())
                                    .amount(p.getUnitPrice())
                                    .imagePath(p.getImagePath())
                                    .build())
                            .toList();

                    return MyProductOrderHistoryResponseDto.builder()
                            .orderId(order.getId())
                            .orderStatus(order.getStatus())
                            .orderedAt(new KstDateTime(order.getOrderedAt()))
                            .products(productInfoList)
                            .build();
                })
                .toList();

        boolean hasNext = dtoList.size() == size;
        Long lastOrderId = hasNext ? dtoList.get(dtoList.size() - 1).getOrderId() : null;

        return MyProductOrderHistoryListResponseDto.builder()
                .orders(dtoList)
                .pagination(new MyProductOrderHistoryListResponseDto.Pagination(size, lastOrderId, hasNext))
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public MyProductOrderResponseDto getProductOrder(Long userId, Long orderId) {

        User user = checkUser(userId);

        ProductOrder productOrder = getOrderWithUserCheck(userId, orderId);
        List<OrderItem> orderItems = orderItemRepository.findByProductOrderId(orderId);

        int refundAmount = calculateRefundAmount(orderItems);

        List<MyProductOrderResponseDto.ProductInfo> productInfo = buildProductInfoList(orderItems);

        return buildOrderDetailResponse(productOrder, productInfo, user, refundAmount);
    }

    @Override
    @Transactional
    public void deleteProductOrder(Long userId, Long orderId) {

        User user = checkUser(userId);

        ProductOrder productOrder = getOrderWithUserCheck(userId, orderId);

        productOrder.deleteOrder();

        productOrderRepository.deleteProductOrder(productOrder, user);
    }

    @Override
    @Transactional
    public ProductOrderConfirmResponseDto confirmProductOrder(Long userId, Long orderId) {

        User user = checkUser(userId);

        ProductOrder productOrder = getOrderWithUserCheck(userId, orderId);
        productOrder.confirm();

        List<OrderItem> orderItems = orderItemRepository.findByProductOrderId(orderId);

        for(OrderItem item : orderItems)
            if(item.getStatus().equals(OrderItemStatus.DELIVERED))
                item.confirm();

        orderItemRepository.confirmOrderItem(orderItems);

        productOrderRepository.confirmProductOrder(productOrder, user);

        return new ProductOrderConfirmResponseDto(productOrder.getId(), productOrder.getStatus());
    }

    @Override
    @Transactional
    public void partialCancelProductOrder(Long userId, Long orderId, ProductOrderPartialCancelRequestDto productOrderPartialCancelRequestDto) {

        User user = checkUser(userId);

        ProductOrder productOrder = getOrderWithUserCheck(userId, orderId);
        productOrder.partialCancel();

        List<OrderItem> cancelItems = cancelItem(productOrder, productOrderPartialCancelRequestDto.getOrderItemIds(), productOrderPartialCancelRequestDto.getCancelReason());
        int refundMoney = cancelItems.stream().mapToInt(OrderItem::getRefundAmount).sum();

        refundPoint(user, refundMoney);
        orderItemRepository.cancelOrderItem(cancelItems);

        productOrderRepository.cancelProductOrder(productOrder, user);

        Payment payment = paymentRepository.findByProductOrderId(productOrder.getId());
        refundPayment(payment, refundMoney, cancelItems.getFirst().getCanceledAt());
    }

    @Override
    @Transactional
    public void cancelProductOrder(Long userId, Long orderId, ProductOrderCancelRequestDto productOrderCancelRequestDto) {

        User user = checkUser(userId);

        ProductOrder productOrder = getOrderWithUserCheck(userId, orderId);
        productOrder.cancel();

        List<OrderItem> cancelItems = cancelItem(productOrder, null, productOrderCancelRequestDto.getCancelReason());
        int refundMoney = cancelItems.stream().mapToInt(OrderItem::getRefundAmount).sum();

        refundPoint(user, refundMoney);
        orderItemRepository.cancelOrderItem(cancelItems);

        productOrderRepository.cancelProductOrder(productOrder, user);

        Payment payment = paymentRepository.findByProductOrderId(productOrder.getId());
        refundPayment(payment, payment.getPaymentAmount(), productOrder.getCanceledAt());
    }

    private User checkUser(Long userId) {
        User user = userRepository.findById(userId);
        user.checkVerifiedUser();

        return user;
    }

    private ProductPromotion getActivePromotion(Long variantId) {
        return productPromotionRepository.findByVariantIdAndStatus(variantId, PromotionStatus.ACTIVE);
    }

    private void restoreStock(OrderItem item) {
        if(item.getPromotionId() != null) {
            ProductPromotion promotion = productPromotionRepository.findById(item.getPromotionId());
            promotion.cancelPromotionSale(item.getQuantity());
            productPromotionRepository.update(promotion);
        } else {
            ProductVariant variant = productVariantRepository.findById(item.getVariantsId());
            variant.cancelSale(item.getQuantity());
            productVariantRepository.update(variant);
        }
    }

    private List<OrderItem> cancelItem(ProductOrder order, List<Long> cancelItemIds, CancelReason reason) {
        List<OrderItem> cancelItems = new ArrayList<>();

        for (OrderItem item : orderItemRepository.findByProductOrderId(order.getId())) {
            if (!item.getStatus().equals(OrderItemStatus.CANCELED) &&
                !item.getStatus().equals(OrderItemStatus.CONFIRMED) &&
                (cancelItemIds == null || cancelItemIds.contains(item.getId()))) {
                item.cancel(reason, LocalDateTime.now());
                restoreStock(item);
                cancelItems.add(item);
            }
        }

        return cancelItems;
    }

    private void refundPoint(User user, int refundAmount) {
        int balance = pointHistoryRepository.findLatestPointHistoryByUserId(user.getId()).getBalanceAfter();
        PointHistory pointHistory = PointHistory.createPointHistory(user.getId(), refundAmount, balance + refundAmount,
                PointActionType.EARN, PointActionReason.PRODUCT_REFUND);
        pointHistoryRepository.save(pointHistory, user);
    }

    private void refundPayment(Payment payment, int amount, LocalDateTime time) {
        payment.refund(amount, time);
        paymentRepository.refund(payment);
    }

    private void validateDuplicateOrder(Long userId, String fingerprint) {
        if (productOrderRepository.existsByUserIdAndFingerprint(userId, fingerprint)) {
            throw new CustomException(ErrorCode.ALREADY_ORDERED);
        }
    }

    private List<OrderItem> createOrderItems(ProductOrderRequestDto dto) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (ProductOrderRequestDto.ServiceProductDto serviceProduct : dto.getProducts()) {
            ProductVariant variant = productVariantRepository.findById(serviceProduct.getVariantId());
            ProductPromotion promotion = null;

            if (variant.isOnPromotion()) {
                promotion = getActivePromotion(variant.getId());
                promotion.isAvailableForPurchase(LocalDateTime.now());
                promotion.reservePromotionStock(serviceProduct.getQuantity());
                productPromotionRepository.update(promotion);
            } else {
                variant.reserveStock(serviceProduct.getQuantity());
                productVariantRepository.update(variant);
            }

            OrderItem orderItem = OrderItem.createOrderItem(variant, promotion, null, serviceProduct.getQuantity());
            orderItems.add(orderItem);
        }

        return orderItems;
    }

    private int calculateTotalAmount(List<OrderItem> items) {
        return items.stream().mapToInt(OrderItem::getOriginalPrice).sum();
    }

    private int calculateTotalDiscount(List<OrderItem> items) {
        return items.stream().mapToInt(OrderItem::getDiscountAmount).sum();
    }

    private ProductOrder saveProductOrder(Long userId, int totalAmount, int discountAmount, String fingerprint, User user) {
        ProductOrder order = ProductOrder.createOrder(userId, totalAmount, discountAmount, fingerprint);
        return productOrderRepository.save(order, user);
    }

    private void saveOrderItems(ProductOrder order, List<OrderItem> items) {
        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemRepository.save(item);
        }
    }

    private ProductOrderResponseDto buildOrderResponse(ProductOrder order, List<OrderItem> items) {
        List<ProductOrderResponseDto.ServiceProductDto> productDtos = items.stream()
                .map(i -> new ProductOrderResponseDto.ServiceProductDto(
                        i.getOrderId(),
                        i.getPromotionId(),
                        i.getOriginalPrice(),
                        i.getQuantity(),
                        i.getDiscountAmount()
                ))
                .toList();

        return new ProductOrderResponseDto(
                order.getId(),
                productDtos,
                order.getSubtotalAmount(),
                order.getStatus(),
                new KstDateTime(order.getOrderedAt())
        );
    }

    private int calculateRefundAmount(List<OrderItem> items) {
        return items.stream().mapToInt(OrderItem::getRefundAmount).sum();
    }

    private List<MyProductOrderResponseDto.ProductInfo> buildProductInfoList(List<OrderItem> items) {
        return items.stream().map(item -> {
            ProductVariant variant = productVariantRepository.findById(item.getVariantsId());
            ProductImage image = productImageRepository.findMainImage(variant.getProductId());

            return new MyProductOrderResponseDto.ProductInfo(
                    item.getId(),
                    variant.getId(),
                    variant.getName(),
                    item.getStatus(),
                    image.getImageUuid(),
                    item.getFinalPrice() / item.getQuantity(),
                    item.getQuantity()
            );
        }).toList();
    }

    private MyProductOrderResponseDto buildOrderDetailResponse(ProductOrder productOrder, List<MyProductOrderResponseDto.ProductInfo> productInfoList, User user, int refundAmount) {
        MyProductOrderResponseDto.OrderInfo orderInfo = new MyProductOrderResponseDto.OrderInfo(productOrder.getId(), productOrder.getStatus(), productOrder.getOrderNumber(), new KstDateTime(productOrder.getOrderedAt()));
        MyProductOrderResponseDto.UserInfo userInfo = new MyProductOrderResponseDto.UserInfo(user.getNicknameKakao(), user.getEmail());
        MyProductOrderResponseDto.PaymentInfo paymentInfo = new MyProductOrderResponseDto.PaymentInfo(PaymentMethod.POINT, productOrder.getSubtotalAmount(), productOrder.getSubtotalAmount() - productOrder.getDiscountAmount(), productOrder.getDiscountAmount());
        MyProductOrderResponseDto.RefundInfo refundInfo = new MyProductOrderResponseDto.RefundInfo(PaymentMethod.POINT, refundAmount);

        return new MyProductOrderResponseDto(orderInfo, productInfoList, userInfo, paymentInfo, refundInfo);
    }

    private ProductOrder getOrderWithUserCheck(Long userId, Long orderId) {
        return productOrderRepository.findByIdAndUserId(orderId, userId);
    }

}
