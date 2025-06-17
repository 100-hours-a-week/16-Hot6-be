package com.kakaotech.ott.ott.productOrder.application.serviceimpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;
import com.kakaotech.ott.ott.orderItem.domain.model.RefundReason;
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
import com.kakaotech.ott.ott.productOrder.presentation.dto.request.ServiceProductDto;
import com.kakaotech.ott.ott.productOrder.presentation.dto.response.*;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemRepository;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
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


    @Override
    @Transactional
    public ProductOrderResponseDto create(ProductOrderRequestDto productOrderRequestDto, Long userId) {

        User user = userRepository.findById(userId);

        user.checkVerifiedUser();

        List<OrderItem> orderItems = new ArrayList<>();
        int totalAmount = 0;
        int orderDiscountAmount = 0;

        for(ServiceProductDto serviceProduct : productOrderRequestDto.getProducts()) {
            Long variantsId = serviceProduct.getProductId();
            Long promotionId = serviceProduct.getPromotionId();
            int originalPrice = serviceProduct.getOriginalPrice();
            int quantity = serviceProduct.getQuantity();
            int productDiscountAmount = serviceProduct.getDiscountPrice();
            int finalPrice = originalPrice - productDiscountAmount;

            ProductVariant productVariant = productVariantRepository.findById(variantsId);

            if (productVariant.isOnPromotion()) {
                ProductPromotion productPromotion = productPromotionRepository.findByVariantIdAndStatus(productVariant.getId(), PromotionStatus.ACTIVE);
                productPromotion.reservePromotionStock(serviceProduct.getQuantity());
                productPromotionRepository.update(productPromotion);
            } else {
                productVariant.reserveStock(quantity);
                // TODO: 예약 재고 증가 호출

                productVariantRepository.update(productVariant);
            }

            // 주문한 상품 총액 계산
            totalAmount += originalPrice * quantity;
            // 할인 금액 계산
            orderDiscountAmount += productDiscountAmount;

            orderItemRepository.existsByProductIdAndPendingProductStatus(variantsId, OrderItemStatus.PENDING, OrderItemStatus.PENDING);

            OrderItem orderItem = OrderItem.createOrderItem(null, variantsId, promotionId, originalPrice, quantity, productDiscountAmount, finalPrice);
            orderItems.add(orderItem);
        }

        // 주문 생성
        ProductOrder productOrder = ProductOrder.createOrder(userId, totalAmount, orderDiscountAmount);
        ProductOrder savedProductOrder = productOrderRepository.save(productOrder, user);

        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(savedProductOrder.getId());
            orderItemRepository.save(orderItem);
        }

        return new ProductOrderResponseDto(
                savedProductOrder.getId(),
                productOrderRequestDto.getProducts(),
                totalAmount,
                productOrder.getStatus(),
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
                                .amount(item.getFinalPrice())
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

        int paymentAmount = orderItems.stream()
                .mapToInt(item -> item.getFinalPrice() * item.getQuantity())
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
                        item.getFinalPrice(),
                        item.getQuantity()
                );
                })
                .toList();

        MyProductOrderResponseDto.UserInfo userInfo = new MyProductOrderResponseDto.UserInfo(user.getNicknameKakao(), user.getEmail());
        MyProductOrderResponseDto.PaymentInfo paymentInfo = new MyProductOrderResponseDto.PaymentInfo("POINT", productOrder.getSubtotalAmount(), paymentAmount, productOrder.getDiscountAmount());
        MyProductOrderResponseDto.RefundInfo refundInfo = new MyProductOrderResponseDto.RefundInfo("POINT", refundAmount);

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

        for (OrderItem item : orderItems) {
            if (!item.getStatus().equals(OrderItemStatus.CONFIRMED) && !item.getStatus().equals(OrderItemStatus.CANCELED)) {

                if (productOrderPartialCancelRequestDto.getOrderItemIds().contains(item.getId())) {
                    item.cancel(RefundReason.CUSTOMER_REQUEST, LocalDateTime.now());
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

        for(OrderItem item : orderItems)
            if(!item.getStatus().equals(OrderItemStatus.CANCELED) && !item.getStatus().equals(OrderItemStatus.CONFIRMED)) {
                item.cancel(RefundReason.CUSTOMER_REQUEST, LocalDateTime.now());
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

        orderItemRepository.cancelOrderItem(orderItems);

        productOrderRepository.cancelProductOrder(productOrder, user);
    }

}
