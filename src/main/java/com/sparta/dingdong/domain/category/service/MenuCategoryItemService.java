package com.sparta.dingdong.domain.category.service;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.domain.category.dto.MenuCategoryDto;

public interface MenuCategoryItemService {
	@Transactional(readOnly = true)
	BaseResponseDto<List<MenuCategoryDto.ItemResponse>> getItemsByCategory(UUID categoryId);

	BaseResponseDto<MenuCategoryDto.ItemResponse> addMenuToCategory(UUID categoryId,
		MenuCategoryDto.ItemRequest req);

	BaseResponseDto<Void> removeMenuFromCategory(UUID menuCategoryItemId);
}
