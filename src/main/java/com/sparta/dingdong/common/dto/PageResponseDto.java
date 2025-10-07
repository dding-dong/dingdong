package com.sparta.dingdong.common.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

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
}
