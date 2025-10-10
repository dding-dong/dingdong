package com.sparta.dingdong.domain.payment.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sparta.dingdong.domain.payment.dto.request.ConfirmPaymentPageRequestDto;
import com.sparta.dingdong.domain.payment.dto.response.ConfirmPaymentPageResponseDto;

import reactor.core.publisher.Mono;

@Service
public class TossPaymentWebClient {

	private final WebClient webClient;

	@Value("${toss.secret-key}")
	String secretKey;

	public TossPaymentWebClient(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl("https://api.tosspayments.com/v1").build();
	}

	public ConfirmPaymentPageResponseDto confirmPayment(ConfirmPaymentPageRequestDto requestDto) {
		String widgetSecretKey = secretKey;
		String authorizations =
			"Basic " + Base64.getEncoder().encodeToString((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));

		return webClient.post()
			.uri("/payments/confirm")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", authorizations)
			.bodyValue(requestDto)
			.retrieve()
			.onStatus(status -> !status.is2xxSuccessful(), clientResponse -> clientResponse.bodyToMono(String.class)
				.flatMap(error -> Mono.error(new RuntimeException("TossPayments API 오류: " + error))))
			.bodyToMono(ConfirmPaymentPageResponseDto.class)
			.block();
	}
}
