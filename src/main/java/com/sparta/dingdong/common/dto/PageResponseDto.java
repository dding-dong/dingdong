package com.sparta.dingdong.common.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Builder;
import lombok.Getter;

@SuppressWarnings("checkstyle:RegexpMultiline")
@Getter
@Builder
public class PageResponseDto<T> {
	private List<T> content; // 실제 데이터
	private long totalElements; // 전체 데이터 개수
	private int totalPages; // 전체 페이지 수
	private int pageNumber; // 현재 페이지 번호
	private int pageSize; // 페이지 크기
	private boolean first; // 첫 페이지 여부
	private boolean last; // 마지막 페이지 여부

	private boolean empty;     // 콘텐츠 비어있는지 여부
	private int numberOfElements; // 현재 페이지 데이터 개수

	public static <T> PageResponseDto<T> from(Page<T> page) {
		return PageResponseDto.<T>builder()
			.content(page.getContent())
			.totalElements(page.getTotalElements())
			.totalPages(page.getTotalPages())
			.pageNumber(page.getNumber())
			.pageSize(page.getSize())
			.first(page.isFirst())
			.last(page.isLast())
			.empty(page.isEmpty())
			.numberOfElements(page.getNumberOfElements())
			.build();
	}
}
