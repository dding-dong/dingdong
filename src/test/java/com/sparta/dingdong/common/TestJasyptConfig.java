package com.sparta.dingdong.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;

@Configuration
public class TestJasyptConfig {

	// 테스트에서는 암호화 무시
	@Bean
	public EncryptablePropertyResolver encryptablePropertyResolver() {
		return property -> property; // 그대로 반환, 복호화 안 함
	}
}
