package com.sparta.dingdong.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class JasyptExample {

	@Value("${jwt.secret}")
	private String secret;

	@PostConstruct
	public void init() {
		// 스프링 컨텍스트 초기화 직후 실행
		System.out.println("=== JWT Secret: " + secret + " ===");
	}
}
