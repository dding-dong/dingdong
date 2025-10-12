package com.sparta.dingdong.domain.payment.service;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.payment.dto.request.CancelPaymentRequestDto;
import com.sparta.dingdong.domain.payment.dto.request.ConfirmPaymentPageRequestDto;
import com.sparta.dingdong.domain.payment.dto.response.ConfirmPaymentPageResponseDto;
import com.sparta.dingdong.domain.payment.dto.response.TossCancelResponseDto;

public interface PaymentPageService {

	ConfirmPaymentPageResponseDto confirmPayment(ConfirmPaymentPageRequestDto requestDto);

	TossCancelResponseDto cancelPayment(CancelPaymentRequestDto cancelPaymentRequestDto, UserAuth userAuth);
}
