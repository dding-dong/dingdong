package com.sparta.dingdong.domain.category.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.category.dto.request.MenuCategoryItemRequestDto;
import com.sparta.dingdong.domain.category.dto.response.MenuCategoryItemResponseDto;

public interface MenuCategoryItemService {

	@Transactional(readOnly = true)
	Page<MenuCategoryItemResponseDto> getItemsByCategory(UUID menuCategoryId, String keyword,
		Pageable pageable);

	MenuCategoryItemResponseDto addMenuToCategory(UUID categoryId, MenuCategoryItemRequestDto req,
		UserAuth user);

	void removeMenuFromCategory(UUID menuCategoryItemId, UserAuth user);
}
