package com.sparta.dingdong.common.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "JwtFilter")
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final JwtExtractor jwtExtractor;
	private final JwtBlacklistService jwtBlacklistService;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {

		String token = jwtExtractor.extractToken(request);

		if (token == null) {
			filterChain.doFilter(request, response);
			return;
		}

		// 블랙리스트 토큰인지 확인
		if (jwtBlacklistService.isBlacklisted(token)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "이미 로그아웃된 토큰입니다.");
			return;
		}

		try {
			if (jwtTokenProvider.validateToken(token)) {
				UserAuth userAuth = jwtTokenProvider.getUserAuth(token);

				List<SimpleGrantedAuthority> authorities = List.of(
					new SimpleGrantedAuthority("ROLE_" + userAuth.getUserRole().name())
				);

				UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(userAuth, null, authorities);

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {
			log.error("JWT 인증 처리 중 예외 발생", e);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
			return;
		}

		filterChain.doFilter(request, response);
	}
}
