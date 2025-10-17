package com.sparta.dingdong.domain.AI.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AIConfig {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
