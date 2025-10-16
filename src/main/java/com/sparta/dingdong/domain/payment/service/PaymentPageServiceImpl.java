package com.sparta.dingdong.domain.payment.service;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.entity.enums.OrderStatus;
import com.sparta.dingdong.domain.order.service.OrderService;
import com.sparta.dingdong.domain.payment.dto.request.CancelPaymentPageRequestDto;
import com.sparta.dingdong.domain.payment.dto.request.CancelPaymentRequestDto;
import com.sparta.dingdong.domain.payment.dto.request.ConfirmPaymentPageRequestDto;
import com.sparta.dingdong.domain.payment.dto.response.ConfirmPaymentPageResponseDto;
import com.sparta.dingdong.domain.payment.dto.response.TossCancelResponseDto;
import com.sparta.dingdong.domain.payment.entity.Payment;
import com.sparta.dingdong.domain.payment.entity.enums.PaymentStatus;
import com.sparta.dingdong.domain.payment.entity.enums.PaymentType;
import com.sparta.dingdong.domain.payment.exception.*;
import com.sparta.dingdong.domain.payment.repository.PaymentRepository;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentPageServiceImpl implements PaymentPageService {

    private final TossPaymentWebClient tossPaymentWebClient;
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final UserService userService;
    private final PaymentTransactionService paymentTransactionService;

    private Payment findPaymentByOrder(Order order) {
        return paymentRepository.findByOrder(order).orElse(null);
    }

    private Payment findPaymentByOrderOrThrow(Order order) {
        return paymentRepository.findByOrder(order).orElseThrow(PaymentNotFoundException::new);
    }

    @Transactional
    @Override
    public ConfirmPaymentPageResponseDto confirmPayment(ConfirmPaymentPageRequestDto requestDto) {
        String orderIdWithTimestamp = validateAndExtractOrderId(requestDto.getOrderId());
        Order order = findOrderOrThrow(orderIdWithTimestamp);

        validatePaymentStatus(order);
        Payment payment = findPaymentByOrderOrThrow(order);
        validateAmount(payment, requestDto.getAmount());

        ConfirmPaymentPageResponseDto responseDto;

        try {
            responseDto = tossPaymentWebClient.confirmPayment(requestDto);

        } catch (TossConfirmPageException ex) {
            paymentTransactionService.markPaymentFailed(payment);
            throw ex;
        }

        updatePaymentAndOrder(payment, order, responseDto, orderIdWithTimestamp);

        return responseDto;
    }

    private String validateAndExtractOrderId(String orderIdWithTimestamp) {
        if (orderIdWithTimestamp == null || !orderIdWithTimestamp.contains("_")) {
            throw new TossConfirmPageException("WRONG_ORDER_ID", orderIdWithTimestamp);
        }
        return orderIdWithTimestamp;
    }

    private Order findOrderOrThrow(String orderIdWithTimestamp) {
        UUID realOrderId = UUID.fromString(orderIdWithTimestamp.split("_")[0]);
        return orderService.findByOrder(realOrderId);
    }

    private void validatePaymentStatus(Order order) {
        Payment payment = findPaymentByOrder(order);
        if (!PaymentStatus.PENDING.equals(payment.getPaymentStatus())) {
            throw new TossConfirmPageException("ALREADY_ORDER_ID", "이미 결제한 주문입니다.");
        }
    }

    private void validateAmount(Payment payment, BigInteger requestedAmount) {
        if (!payment.getAmount().equals(requestedAmount)) {
            throw new TossConfirmPageException("MISS_MATCH_AMOUNT", "요청한 값이랑 결제 값이 다릅니다");
        }
    }

    private void updatePaymentAndOrder(
            Payment payment,
            Order order,
            ConfirmPaymentPageResponseDto tossConfirmResponse,
            String orderIdWithTimestamp
    ) {
        payment.setTossOrderId(orderIdWithTimestamp);
        payment.setApprovedAt(tossConfirmResponse.getApprovedAt());
        payment.changeStatus(PaymentStatus.PAID);
        order.changeStatus(OrderStatus.REQUESTED);

        order.setRequestedAt((LocalDateTime.now()));

        payment.confirmSuccess(
                tossConfirmResponse.getPaymentKey(),
                PaymentType.from(tossConfirmResponse.getType())
        );
    }

    @Override
    @Transactional
    public TossCancelResponseDto cancelPayment(CancelPaymentRequestDto requestDto, UserAuth userAuth) {
        User user = userService.findByUser(userAuth);

        Order order = orderService.findByOrder(requestDto.getOrderId());
        Payment payment = findPaymentByOrder(order);

        if (!user.equals(order.getUser())) {
            throw new NotOwnerPaymentException("해당 유저의 결제가 아닙니다");
        }

        if (payment == null) {
            throw new PaymentNotFoundException();
        }

        if (payment.getTossOrderId() == null) {
            throw new NotTossPaymentOrderException("토스 페에지에서 결제한 주문이 아닙니다.");
        }

        CancelPaymentPageRequestDto tossCancelRequestDto = CancelPaymentPageRequestDto.builder()
                .cancelReason(requestDto.getRefundReason())
                .build();

        TossCancelResponseDto tossCancelResponseDto = tossPaymentWebClient.cancelPayment(tossCancelRequestDto,
                payment.getPaymentKey());

        payment.changeStatus(PaymentStatus.REFUNDED);
        payment.setRefundReason(requestDto.getRefundReason());

        return tossCancelResponseDto;
    }

    private Payment findByPaymentOrNull(Order order) {
        return paymentRepository.findByOrder(order).orElse(null);
    }

    @Override
    @Transactional
    public void createPayment(User user, Order order) {

        // 주문에 대한 결제가 이미 있으면 예외 처리해준다.
        Payment existingPayment = findByPaymentOrNull(order);

        // order에 대한 payment가 있는데 paymentStatus가 Failed 상태가 아니면 PaymentAlreadyExistsException();
        // order에 대한 payment가 있는데 paymentStatus가 Failed면 Pending상태로 바꿔준다.
        if (existingPayment != null) {

            if (existingPayment.getPaymentStatus().equals(PaymentStatus.FAILED)) {
                existingPayment.deleteFailReason();
                existingPayment.changeStatus(PaymentStatus.PENDING);
                paymentRepository.save(existingPayment);
                return;

            } else if (existingPayment.getPaymentStatus().equals(PaymentStatus.REFUNDED)
                    || existingPayment.getPaymentStatus().equals(PaymentStatus.PAID)) {
                throw new PaymentAlreadyExistsException();

            } else {
                return;
            }
        }

        // 이 두개를 짬뽕해서 Payment로 만들어준다. Payment 상태값을 Pending으로 저장.
        Payment payment = Payment.createPayment(user, order);

        // Payment 저장
        paymentRepository.save(payment);
    }
}
