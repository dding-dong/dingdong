package com.sparta.dingdong.domain.category.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.category.dto.request.MenuCategoryRequestDto;
import com.sparta.dingdong.domain.category.dto.response.MenuCategoryResponseDto;

public interface MenuCategoryService {

	@Transactional(readOnly = true)
	Page<MenuCategoryResponseDto> getByStore(UUID storeId, String keyword, Pageable pageable);

	MenuCategoryResponseDto create(UUID storeId, MenuCategoryRequestDto req, UserAuth user);

	MenuCategoryResponseDto update(UUID categoryId, MenuCategoryRequestDto req, UserAuth user);

	void delete(UUID categoryId, UserAuth user);

	MenuCategoryResponseDto restore(UUID categoryId, UserAuth user);
}
