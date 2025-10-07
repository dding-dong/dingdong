package com.sparta.dingdong.domain.category.service;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.category.dto.request.MenuCategoryRequestDto;
import com.sparta.dingdong.domain.category.dto.response.MenuCategoryResponseDto;

public interface MenuCategoryService {
	@Transactional(readOnly = true)
	List<MenuCategoryResponseDto> getByStore(UUID storeId);

	MenuCategoryResponseDto create(UUID storeId, MenuCategoryRequestDto req, UserAuth user);

	MenuCategoryResponseDto update(UUID categoryId, MenuCategoryRequestDto req, UserAuth user);

	void delete(UUID categoryId, UserAuth user);

	MenuCategoryResponseDto restore(UUID categoryId, UserAuth user);
}
