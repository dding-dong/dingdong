package com.sparta.dingdong.common.config;

import org.springframework.stereotype.Component;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

@Component
public class DotEnvLoader {

	@PostConstruct
	public void load() {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		System.setProperty("GOOGLE_API_KEY", dotenv.get("GOOGLE_API_KEY"));
	}
}
