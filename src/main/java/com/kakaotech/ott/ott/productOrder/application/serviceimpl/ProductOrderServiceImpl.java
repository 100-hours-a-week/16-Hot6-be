package com.kakaotech.ott.ott.productOrder.application.serviceimpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;
import com.kakaotech.ott.ott.orderItem.domain.model.RefundReason;
import com.kakaotech.ott.ott.payment.domain.model.PaymentMethod;
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
import com.kakaotech.ott.ott.productOrder.presentation.dto.request.ProductOrderPartialCancelRequestDto;
import com.kakaotech.ott.ott.productOrder.presentation.dto.request.ProductOrderRequestDto;
import com.kakaotech.ott.ott.productOrder.presentation.dto.response.*;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemRepository;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import com.kakaotech.ott.ott.util.validator.OrderFingerprintUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

// 주문 요청 들어오면 해당 상품 id값을 조회하여
    @Override
    @Transactional
    public ProductOrderResponseDto create(ProductOrderRequestDto productOrderRequestDto, Long userId) {

        User user = userRepository.findById(userId);

        user.checkVerifiedUser();

        // 중복 주문인지 확인(주문 상품 조합으로 확인)
        String fingerprint = OrderFingerprintUtil.generateFingerprint(productOrderRequestDto.getProducts());
        boolean exists = productOrderRepository.existsByUserIdAndFingerprint(userId, fingerprint);

        if(exists) {
            throw new CustomException(ErrorCode.ALREADY_ORDERED);
        }

        List<OrderItem> orderItems = new ArrayList<>();
        int totalAmount = 0;
        int orderDiscountAmount = 0;

        for(ProductOrderRequestDto.ServiceProductDto serviceProduct : productOrderRequestDto.getProducts()) {
            Long variantId = serviceProduct.getVariantId();
            int quantity = serviceProduct.getQuantity();
            int productDiscountAmount = 0;
            int productDiscountPrice = 0;
            Long promotionId = null;

            ProductVariant productVariant = productVariantRepository.findById(variantId);
            totalAmount += productVariant.getPrice() * quantity;

            if (productVariant.isOnPromotion()) {
                ProductPromotion productPromotion = productPromotionRepository.findByVariantIdAndStatus(productVariant.getId(), PromotionStatus.ACTIVE);
                productPromotion.reservePromotionStock(serviceProduct.getQuantity());
                productPromotionRepository.update(productPromotion);

                promotionId = productPromotion.getId();

                // 할인 금액 특가일 때만 계산
                productDiscountPrice = productPromotion.getDiscountPrice();
                productDiscountAmount = productVariant.getPrice() - productDiscountPrice;
                orderDiscountAmount += productDiscountAmount * quantity;


            } else {
                productVariant.reserveStock(quantity);

                productVariantRepository.update(productVariant);
            }

            OrderItem orderItem = OrderItem.createOrderItem(null, variantId, promotionId, productVariant.getPrice() * quantity,
                    quantity, productDiscountAmount * quantity, productDiscountPrice != 0 ? productDiscountPrice * quantity : productVariant.getPrice() * quantity);
            orderItems.add(orderItem);


        }

        // 주문 생성
        ProductOrder productOrder = ProductOrder.createOrder(userId, totalAmount, orderDiscountAmount, fingerprint);
        ProductOrder savedProductOrder = productOrderRepository.save(productOrder, user);

        List<ProductOrderResponseDto.ServiceProductDto> serviceProductDtos = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(savedProductOrder.getId());
            orderItemRepository.save(orderItem);
            serviceProductDtos.add(new ProductOrderResponseDto.ServiceProductDto(orderItem.getOrderId(), orderItem.getPromotionId(), orderItem.getOriginalPrice(), orderItem.getQuantity(), orderItem.getDiscountAmount()));
        }

        return new ProductOrderResponseDto(
                savedProductOrder.getId(),
                serviceProductDtos,
                totalAmount,
                savedProductOrder.getStatus(),
                savedProductOrder.getOrderedAt());
    }

    @Override
    @Transactional(readOnly = true)
    public MyProductOrderHistoryListResponseDto getProductOrderHistory(Long userId, Long lastId, int size) {

        User user = userRepository.findById(userId);

        user.checkVerifiedUser();

        Slice<ProductOrder> orders = productOrderRepository.findAllByUserId(userId, lastId, size);

        List<MyProductOrderHistoryResponseDto> dtoList = orders.stream().map(order -> {
            List<OrderItem> orderItems = orderItemRepository.findByProductOrderId(order.getId());
            List<MyProductOrderHistoryResponseDto.ProductDto> products = orderItems.stream()
                    .map(item -> {
                        ProductVariant productVariant = productVariantRepository.findById(item.getVariantsId());
                        ProductImage productImage = productImageRepository.findMainImage(productVariant.getProductId());

                        return MyProductOrderHistoryResponseDto.ProductDto.builder()
                                .productId(productVariant.getId())
                                .status(item.getStatus().name())
                                .productName(productVariant.getName()) // TODO: 실제 값으로 교체
                                .quantity(item.getQuantity())
                                .amount(item.getFinalPrice()/item.getQuantity())
                                .imagePath(productImage.getImageUuid()) // TODO: 실제 값으로 교체
                                .build();
                    })
                    .toList();

            return MyProductOrderHistoryResponseDto.builder()
                    .orderId(order.getId())
                    .orderStatus(order.getStatus())
                    .orderedAt(order.getOrderedAt())
                    .products(products)
                    .build();
        }).toList();

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

        User user = userRepository.findById(userId);

        user.checkVerifiedUser();

        ProductOrder productOrder = productOrderRepository.findByIdAndUserId(orderId, userId);
        List<OrderItem> orderItems = orderItemRepository.findByProductOrderId(orderId);

        MyProductOrderResponseDto.OrderInfo orderInfo = new MyProductOrderResponseDto.OrderInfo(productOrder.getId(), productOrder.getStatus(), productOrder.getOrderNumber(), productOrder.getOrderedAt());

        int refundAmount = orderItems.stream()
                .mapToInt(OrderItem::getRefundAmount)
                .sum();

        List<MyProductOrderResponseDto.ProductInfo> productInfo = orderItems.stream()
                .map(item -> {
                        ProductVariant productVariant = productVariantRepository.findById(item.getVariantsId());
                        ProductImage productImage = productImageRepository.findMainImage(productVariant.getProductId());

                        return new MyProductOrderResponseDto.ProductInfo(
                        item.getId(),
                        productVariant.getId(),
                        productVariant.getName(),
                        item.getStatus(),
                        productImage.getImageUuid(),
                        item.getFinalPrice()/item.getQuantity(),
                        item.getQuantity()
                );
                })
                .toList();

        int totalPrice = productOrder.getSubtotalAmount();
        int discountAmount = productOrder.getDiscountAmount();
        int paymentAmount = totalPrice - discountAmount;

        MyProductOrderResponseDto.UserInfo userInfo = new MyProductOrderResponseDto.UserInfo(user.getNicknameKakao(), user.getEmail());
        MyProductOrderResponseDto.PaymentInfo paymentInfo = new MyProductOrderResponseDto.PaymentInfo(PaymentMethod.POINT, totalPrice, paymentAmount, discountAmount);
        MyProductOrderResponseDto.RefundInfo refundInfo = new MyProductOrderResponseDto.RefundInfo(PaymentMethod.POINT, refundAmount);

        return new MyProductOrderResponseDto(orderInfo, productInfo, userInfo, paymentInfo, refundInfo);
    }

    @Override
    @Transactional
    public void deleteProductOrder(Long userId, Long orderId) {

        User user = userRepository.findById(userId);

        user.checkVerifiedUser();

        ProductOrder productOrder = productOrderRepository.findByIdAndUserId(orderId, userId);

        productOrder.deleteOrder();

        productOrderRepository.deleteProductOrder(productOrder, user);
    }

    @Override
    @Transactional
    public ProductOrderConfirmResponseDto confirmProductOrder(Long userId, Long orderId) {

        User user = userRepository.findById(userId);

        user.checkVerifiedUser();

        ProductOrder productOrder = productOrderRepository.findByIdAndUserId(orderId, userId);
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

        User user = userRepository.findById(userId);

        user.checkVerifiedUser();

        ProductOrder productOrder = productOrderRepository.findByIdAndUserId(orderId, userId);
        productOrder.partialCancel();

        List<OrderItem> orderItems = orderItemRepository.findByProductOrderId(orderId);
        List<OrderItem> cancelItems = new ArrayList<>(productOrderPartialCancelRequestDto.getOrderItemIds().size());

        int refundMoney = 0;

        for (OrderItem item : orderItems) {
            if (!item.getStatus().equals(OrderItemStatus.CONFIRMED) && !item.getStatus().equals(OrderItemStatus.CANCELED)) {

                if (productOrderPartialCancelRequestDto.getOrderItemIds().contains(item.getId())) {
                    item.cancel(RefundReason.CUSTOMER_REQUEST, LocalDateTime.now());
                    refundMoney += item.getRefundAmount();

                    cancelItems.add(item);
                    ProductVariant productVariant = productVariantRepository.findById(item.getVariantsId());
                    if (productVariant.isOnPromotion()) {
                        ProductPromotion productPromotion = productPromotionRepository.findByVariantIdAndStatus(productVariant.getId(), PromotionStatus.ACTIVE);
                        productPromotion.cancelPromotionSale(item.getQuantity());
                        productPromotionRepository.update(productPromotion);
                    } else {
                        productVariant.cancelSale(item.getQuantity());
                        productVariantRepository.update(productVariant);
                    }
                }
            }

        }

        int balanceAmount = pointHistoryRepository.findLatestPointHistoryByUserId(userId).getBalanceAfter();
        PointHistory pointHistory = PointHistory.createPointHistory(userId, refundMoney, balanceAmount + refundMoney, PointActionType.EARN, PointActionReason.PRODUCT_REFUND);
        pointHistoryRepository.save(pointHistory, user);

        orderItemRepository.cancelOrderItem(cancelItems);

        productOrderRepository.cancelProductOrder(productOrder, user);
    }

    @Override
    @Transactional
    public void cancelProductOrder(Long userId, Long orderId) {

        User user = userRepository.findById(userId);

        user.checkVerifiedUser();

        ProductOrder productOrder = productOrderRepository.findByIdAndUserId(orderId, userId);
        productOrder.cancel();

        List<OrderItem> orderItems = orderItemRepository.findByProductOrderId(orderId);

        int refundMoney = 0;

        for(OrderItem item : orderItems)
            if(!item.getStatus().equals(OrderItemStatus.CANCELED) && !item.getStatus().equals(OrderItemStatus.CONFIRMED)) {
                item.cancel(RefundReason.CUSTOMER_REQUEST, LocalDateTime.now());
                refundMoney += item.getRefundAmount();

                if (item.getPromotionId() != null) {
                    ProductPromotion productPromotion = productPromotionRepository.findById(item.getPromotionId());
                    productPromotion.cancelPromotionSale(item.getQuantity());
                    productPromotionRepository.update(productPromotion);
                } else {
                    ProductVariant productVariant = productVariantRepository.findById(item.getVariantsId());
                    productVariant.cancelSale(item.getQuantity());
                    productVariantRepository.update(productVariant);
                }
            }

        int userPoint = pointHistoryRepository.findLatestPointHistoryByUserId(userId).getBalanceAfter();
        PointHistory pointHistory = PointHistory.createPointHistory(userId, refundMoney, userPoint + refundMoney, PointActionType.EARN, PointActionReason.PRODUCT_REFUND);
        pointHistoryRepository.save(pointHistory, user);

        orderItemRepository.cancelOrderItem(orderItems);

        productOrderRepository.cancelProductOrder(productOrder, user);
    }

}
