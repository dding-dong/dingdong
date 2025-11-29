package com.sparta.dingdong.domain.AI.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sparta.dingdong.domain.AI.config.GoogleAIProperties;
import com.sparta.dingdong.domain.AI.exception.GoogleApiCallException;
import com.sparta.dingdong.domain.AI.exception.GoogleApiKeyMissingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAIService {

	private final WebClient webClient;
	private final GoogleAIProperties googleAIProperties;

	/**
	 * Google Gemini API를 논블로킹 방식으로 호출하여 텍스트 생성
	 */
	public Mono<String> generateText(String prompt) {
		String apiKey = googleAIProperties.getApiKey();
		String apiUrl = googleAIProperties.getUrl();

		if (apiKey == null || apiKey.isBlank()) {
			return Mono.error(new GoogleApiKeyMissingException());
		}

		// 요청 바디 구성
		Map<String, Object> requestBody = Map.of(
			"contents", List.of(
				Map.of(
					"role", "user",
					"parts", List.of(
						Map.of("text", prompt)
					)
				)
			)
		);

		return webClient.post()
			.uri(apiUrl)
			.contentType(MediaType.APPLICATION_JSON)
			.header("x-goog-api-key", apiKey)
			.bodyValue(requestBody)
			.retrieve()
			.bodyToMono(String.class)
			.map(this::extracText)
			.onErrorMap(e -> {
				log.error("[GoogleAIService] API 호출 실패: {}", e.getMessage());
				return new GoogleApiCallException("AI 응답 생성 중 오류가 발생했습니다.", e);
			});
	}

	/**
	 * Gemini 응답 JSON -> 텍스트 추출
	 */
	public String extracText(String responseBody) {
		JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
		return json.getAsJsonArray("candidates")
			.get(0).getAsJsonObject()
			.getAsJsonObject("content")
			.getAsJsonArray("parts")
			.get(0).getAsJsonObject()
			.get("text").getAsString();
	}
}
