package com.sparta.dingdong.domain.AI.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sparta.dingdong.domain.AI.config.GoogleAIProperties;
import com.sparta.dingdong.domain.AI.exception.GoogleApiCallException;
import com.sparta.dingdong.domain.AI.exception.GoogleApiKeyMissingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAIService {

	private final RestTemplate restTemplate;
	private final GoogleAIProperties googleAIProperties;

	/**
	 * Google Gemini API를 호출하여 텍스트를 생성합니다.
	 * @param prompt 사용자 프롬프트
	 * @return AI 생성 텍스트
	 */
	public String generateText(String prompt) {
		String apiKey = googleAIProperties.getApiKey();
		String apiUrl = googleAIProperties.getUrl();

		try {
			if (apiKey == null || apiKey.isBlank()) {
				throw new GoogleApiKeyMissingException();
			}

			// 요청 헤더 구성
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("x-goog-api-key", apiKey); // API 키 인증 방식

			// 요청 바디 구성
			Map<String, Object> content = new HashMap<>();
			content.put("parts", List.of(Map.of("text", prompt)));

			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("contents", List.of(content));

			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

			// API 호출
			ResponseEntity<String> response =
				restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

			// 응답 파싱
			JsonObject json = JsonParser.parseString(response.getBody()).getAsJsonObject();

			return json.getAsJsonArray("candidates")
				.get(0).getAsJsonObject()
				.getAsJsonObject("content")
				.getAsJsonArray("parts")
				.get(0).getAsJsonObject()
				.get("text").getAsString();

		} catch (Exception e) {
			log.error("[GoogleAIService] Google AI API 호출 실패: {}", e.getMessage());
			throw new GoogleApiCallException("AI 응답 생성 중 오류가 발생했습니다.", e);
		}
	}
}
