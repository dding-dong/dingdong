package com.sparta.dingdong.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtils {

	private static final int DEFAULT_SIZE = 10;
	private static final int[] ALLOWED_SIZES = {10, 30, 50};

	public static Pageable fixedPageable(Pageable pageable, String defaultSortField) {
		int pageSize = pageable.getPageSize();

		// 허용되지 않은 페이지 사이즈면 기본값으로 변경
		boolean valid = false;
		for (int allowed : ALLOWED_SIZES) {
			if (allowed == pageSize) {
				valid = true;
				break;
			}
		}
		if (!valid) {
			pageSize = DEFAULT_SIZE;
		}

		// 기본 정렬 적용
		Sort sort = pageable.getSort().isSorted()
			? pageable.getSort()
			: Sort.by(Sort.Direction.DESC, defaultSortField);

		return PageRequest.of(pageable.getPageNumber(), pageSize, sort);
	}
}
