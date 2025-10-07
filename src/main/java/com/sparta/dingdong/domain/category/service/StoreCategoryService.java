package com.sparta.dingdong.domain.category.service;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.domain.category.dto.request.StoreCategoryRequestDto;
import com.sparta.dingdong.domain.category.dto.response.StoreCategoryResponseDto;

public interface StoreCategoryService {
	@Transactional(readOnly = true)
	List<StoreCategoryResponseDto> getAll();

	@Transactional(readOnly = true)
	StoreCategoryResponseDto getById(UUID id);

	StoreCategoryResponseDto create(StoreCategoryRequestDto req);

	StoreCategoryResponseDto update(UUID id, StoreCategoryRequestDto req);

	void delete(UUID id);

	StoreCategoryResponseDto restore(UUID id);
}
