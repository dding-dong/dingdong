package com.sparta.dingdong.domain.payment.service;

import com.sparta.dingdong.domain.payment.dto.request.ConfirmPaymentPageRequestDto;
import com.sparta.dingdong.domain.payment.dto.response.ConfirmPaymentPageResponseDto;

public interface PaymentPageService {

	ConfirmPaymentPageResponseDto confirmPayment(ConfirmPaymentPageRequestDto requestDto);
}
