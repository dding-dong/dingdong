package com.sparta.dingdong.common.jwt;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.dingdong.common.dto.BaseResponseDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	// 권한 부여 실패(로그인은 되어있지만 ROLE_ADMIN만 접근 가능한 페이지에 ROLE_USER가 요청한 경우)
	@Override
	public void handle(HttpServletRequest request,
		HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);

		BaseResponseDto<Void> errorResponse = BaseResponseDto.error(CommonErrorCode.ACCESS_DENIED);

		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}

