package com.kakaotech.ott.ott.payment.application.serviceImpl;

import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.model.RefundReason;
import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemRepository;
import com.kakaotech.ott.ott.payment.application.event.PaymentCompletedEvent;
import com.kakaotech.ott.ott.payment.application.service.PaymentService;
import com.kakaotech.ott.ott.payment.domain.model.Payment;
import com.kakaotech.ott.ott.payment.domain.model.PaymentMethod;
import com.kakaotech.ott.ott.payment.domain.repository.PaymentRepository;
import com.kakaotech.ott.ott.payment.presentation.dto.request.PaymentRequestDto;
import com.kakaotech.ott.ott.payment.presentation.dto.request.RefundRequestDto;
import com.kakaotech.ott.ott.payment.presentation.dto.response.PaymentResponseDto;
import com.kakaotech.ott.ott.payment.presentation.dto.response.RefundResponseDto;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointHistory;
import com.kakaotech.ott.ott.pointHistory.domain.repository.PointHistoryRepository;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderRepository;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import com.kakaotech.ott.ott.util.KstDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ProductOrderRepository productOrderRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto, Long userId, Long orderId) {
        User user = userRepository.findById(userId);

        PointHistory pointHistory = pointHistoryRepository.findLatestPointHistoryByUserId(userId);
        PointHistory newPointHistory = pointHistory.deduct(paymentRequestDto.getPoint());
        PointHistory savedPointHistory = pointHistoryRepository.save(newPointHistory, user);

        Payment payment = Payment.createPayment(orderId, PaymentMethod.POINT, paymentRequestDto.getPoint());
        Payment savedPayment = paymentRepository.save(payment, user);

        eventPublisher.publishEvent(new PaymentCompletedEvent(orderId, userId, paymentRequestDto.getPoint()));

        return new PaymentResponseDto(savedPointHistory.getId(), orderId, savedPayment.getPaymentAmount(), new KstDateTime(savedPayment.getPaidAt()));
    }

    @Override
    @Transactional
    public RefundResponseDto refundPayment(RefundRequestDto refundRequestDto, Long userId, Long orderId) {
        User user = userRepository.findById(userId);
        user.checkVerifiedUser();
        // 해당 주문을 생성한 사용자인지 확인
        ProductOrder productOrder = productOrderRepository.findByIdAndUserId(orderId, userId);
        List<OrderItem> orderItems = orderItemRepository.findByProductOrderId(orderId);

        // DTO 리스트에서 환불사유 Map 구성
        Map<Long, RefundReason> refundReasonMap = refundRequestDto.getRefundItems().stream()
                .collect(Collectors.toMap(
                        RefundRequestDto.RefundItemRequest::getOrderItemId,
                        RefundRequestDto.RefundItemRequest::getReason
                ));

        for (OrderItem orderItem : orderItems) {
            Long itemId = orderItem.getId();
            if (refundReasonMap.containsKey(itemId)) {
                orderItem.refundRequest(refundReasonMap.get(itemId), LocalDateTime.now());
            }
        }

        orderItemRepository.refundRequestOrderItem(orderItems);
        return new RefundResponseDto(orderId);
    }
}
