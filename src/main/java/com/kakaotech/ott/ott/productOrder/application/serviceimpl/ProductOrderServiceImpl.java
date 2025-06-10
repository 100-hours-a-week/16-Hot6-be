package com.kakaotech.ott.ott.productOrder.application.serviceimpl;

import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;
import com.kakaotech.ott.ott.orderItem.domain.model.RefundReason;
import com.kakaotech.ott.ott.productOrder.application.service.ProductOrderService;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderRepository;
import com.kakaotech.ott.ott.productOrder.presentation.dto.request.ProductOrderPartialCancelRequestDto;
import com.kakaotech.ott.ott.productOrder.presentation.dto.request.ProductOrderPartialConfirmRequestDto;
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


    @Override
    @Transactional
    public ProductOrderResponseDto create(ProductOrderRequestDto productOrderRequestDto, Long userId) {

        User user = userRepository.findById(userId);

        List<OrderItem> orderItems = new ArrayList<>();
        int totalAmount = 0;
        int discountAmount = 0;

        for(ServiceProductDto serviceProduct : productOrderRequestDto.getProduct()) {
            Long productId = serviceProduct.getProductId();
            int price = serviceProduct.getPrice();
            int quantity = serviceProduct.getQuantity();

            // 주문한 상품 총액 계산
            totalAmount += price * quantity;
            // 할인 금액 계산
            //discountAmount += ???

            orderItemRepository.existsByProductIdAndPendingProductStatus(productId, OrderItemStatus.PENDING);

            OrderItem orderItem = OrderItem.createOrderItem(null, productId, price, quantity);
            orderItems.add(orderItem);
        }

        // 주문 생성
        ProductOrder productOrder = ProductOrder.createOrder(userId, totalAmount, 0);
        ProductOrder savedProductOrder = productOrderRepository.save(productOrder, user);

        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(savedProductOrder.getId());
            orderItemRepository.save(orderItem);
        }

        return new ProductOrderResponseDto(
                savedProductOrder.getId(),
                productOrderRequestDto.getProduct(),
                totalAmount,
                productOrder.getStatus(),
                savedProductOrder.getOrderedAt());
    }

    @Override
    @Transactional(readOnly = true)
    public MyProductOrderHistoryListResponseDto getProductOrderHistory(Long userId, Long lastId, int size) {
        Slice<ProductOrder> orders = productOrderRepository.findAllByUserId(userId, lastId, size);

        List<MyProductOrderHistoryResponseDto> dtoList = orders.stream().map(order -> {
            List<OrderItem> orderItems = orderItemRepository.findByProductOrderId(order.getId());
            List<MyProductOrderHistoryResponseDto.ProductDto> products = orderItems.stream()
                    .map(item -> MyProductOrderHistoryResponseDto.ProductDto.builder()
                            .productId(item.getProductId())
                            .status(item.getStatus().name())
                            .productName("몰루") // TODO: 실제 값으로 교체
                            .quantity(item.getQuantity())
                            .amount(item.getPrice())
                            .imagePath("몰라") // TODO: 실제 값으로 교체
                            .build())
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

        ProductOrder productOrder = productOrderRepository.findByIdAndUserId(orderId, userId);
        List<OrderItem> orderItems = orderItemRepository.findByProductOrderId(orderId);

        MyProductOrderResponseDto.OrderInfo orderInfo = new MyProductOrderResponseDto.OrderInfo(productOrder.getId(), productOrder.getStatus(), productOrder.getOrderNumber(), productOrder.getOrderedAt());

        int refundAmount = orderItems.stream()
                .mapToInt(OrderItem::getRefundAmount)
                .sum();

        int paymentAmount = orderItems.stream()
                .mapToInt(item -> item.getPrice() * item.getQuantity())
                .sum();

        List<MyProductOrderResponseDto.ProductInfo> productInfo = orderItems.stream()
                .map(item ->
                        new MyProductOrderResponseDto.ProductInfo(
                        item.getId(),
                        item.getProductId(),
                        "상품명",
                        item.getStatus(),
                        "image.png",
                        item.getPrice(),
                        item.getQuantity()
                ))
                .toList();

        MyProductOrderResponseDto.UserInfo userInfo = new MyProductOrderResponseDto.UserInfo(user.getNicknameKakao(), user.getEmail());
        MyProductOrderResponseDto.PaymentInfo paymentInfo = new MyProductOrderResponseDto.PaymentInfo("POINT", paymentAmount, 0);
        MyProductOrderResponseDto.RefundInfo refundInfo = new MyProductOrderResponseDto.RefundInfo("POINT", refundAmount);

        return new MyProductOrderResponseDto(orderInfo, productInfo, userInfo, paymentInfo, refundInfo);
    }

    @Override
    @Transactional
    public void deleteProductOrder(Long userId, Long orderId) {

        User user = userRepository.findById(userId);

        ProductOrder productOrder = productOrderRepository.findByIdAndUserId(orderId, userId);

        productOrder.deleteOrder();

        productOrderRepository.deleteProductOrder(productOrder, user);
    }

    @Override
    @Transactional
    public ProductOrderConfirmResponseDto confirmProductOrder(Long userId, Long orderId) {

        User user = userRepository.findById(userId);

        ProductOrder productOrder = productOrderRepository.findByIdAndUserId(orderId, userId);
        productOrder.confirm();

        List<OrderItem> orderItems = orderItemRepository.findByProductOrderId(orderId);

        for(OrderItem item : orderItems)
            if(!item.getStatus().equals(OrderItemStatus.DELIVERED))
                item.confirm();

        orderItemRepository.confirmOrderItem(orderItems);

        productOrderRepository.confirmProductOrder(productOrder, user);

        return new ProductOrderConfirmResponseDto(productOrder.getId(), productOrder.getStatus());
    }

    @Override
    @Transactional
    public void partialCancelProductOrder(Long userId, Long orderId, ProductOrderPartialCancelRequestDto productOrderPartialCancelRequestDto) {

        User user = userRepository.findById(userId);

        ProductOrder productOrder = productOrderRepository.findByIdAndUserId(orderId, userId);
        productOrder.partialCancel();

        List<OrderItem> orderItems = orderItemRepository.findByProductOrderId(orderId);

        for (OrderItem item : orderItems) {
            if (productOrderPartialCancelRequestDto.getOrderItemIds().contains(item.getId())) {
                item.cancel(RefundReason.CUSTOMER_REQUEST, LocalDateTime.now());
            }
        }

        orderItemRepository.cancelOrderItem(orderItems);

        productOrderRepository.cancelProductOrder(productOrder, user);
    }

    @Override
    @Transactional
    public void cancelProductOrder(Long userId, Long orderId) {

        User user = userRepository.findById(userId);

        ProductOrder productOrder = productOrderRepository.findByIdAndUserId(orderId, userId);
        productOrder.cancel();

        List<OrderItem> orderItems = orderItemRepository.findByProductOrderId(orderId);

        for(OrderItem item : orderItems)
            if(!item.getStatus().equals(OrderItemStatus.CANCELED))
                item.cancel(RefundReason.CUSTOMER_REQUEST, LocalDateTime.now());

        orderItemRepository.cancelOrderItem(orderItems);

        productOrderRepository.cancelProductOrder(productOrder, user);
    }

}
