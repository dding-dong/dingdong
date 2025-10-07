package com.sparta.dingdong.common.jwt;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.dingdong.common.dto.BaseResponseDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// JwtAuthenticationEntryPoint가 없으면 인증실패시 시큐리티가 소셜로그인으로 리다이렉트
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException authException) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		BaseResponseDto<?> error = BaseResponseDto.error(CommonErrorCode.UNAUTHORIZED);

		response.getWriter().write(objectMapper.writeValueAsString(error));
	}
}
