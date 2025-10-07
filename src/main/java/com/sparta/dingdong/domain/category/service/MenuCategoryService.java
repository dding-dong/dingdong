package com.sparta.dingdong.domain.category.service;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.domain.category.dto.request.MenuCategoryRequestDto;
import com.sparta.dingdong.domain.category.dto.response.MenuCategoryResponseDto;

public interface MenuCategoryService {
	@Transactional(readOnly = true)
	List<MenuCategoryResponseDto> getByStore(UUID storeId);

	MenuCategoryResponseDto create(UUID storeId, MenuCategoryRequestDto req);

	MenuCategoryResponseDto update(UUID categoryId, MenuCategoryRequestDto req);

	void delete(UUID categoryId);

	MenuCategoryResponseDto restore(UUID categoryId);
}
