package com.sparta.dingdong.domain.review.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class HelloControllerV1 {

	@GetMapping("/")
	public String home() {
		return "index";
	}
}
