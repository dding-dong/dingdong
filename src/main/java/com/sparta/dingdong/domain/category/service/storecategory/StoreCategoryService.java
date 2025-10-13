package com.sparta.dingdong.domain.category.service.storecategory;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.category.dto.request.StoreCategoryRequestDto;
import com.sparta.dingdong.domain.category.dto.response.StoreCategoryResponseDto;

public interface StoreCategoryService {

	@Transactional(readOnly = true)
	Page<StoreCategoryResponseDto> getAll(String keyword, Pageable pageable);

	@Transactional(readOnly = true)
	StoreCategoryResponseDto getById(UUID id);

	StoreCategoryResponseDto create(StoreCategoryRequestDto req);

	StoreCategoryResponseDto update(UUID id, StoreCategoryRequestDto req);

	void delete(UUID id, UserAuth user);

	StoreCategoryResponseDto restore(UUID id, UserAuth user);
}
