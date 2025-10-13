package com.sparta.dingdong.domain.payment.dto.response;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.Getter;

@Getter
public class ConfirmPaymentPageResponseDto {
	private String paymentKey;
	private String orderId;
	private String orderName;
	private String method;
	private String type;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime approvedAt;

	@JsonSetter("approvedAt")
	public void setApprovedAt(String approvedAt) {
		this.approvedAt = OffsetDateTime.parse(approvedAt).toLocalDateTime();
	}
}
