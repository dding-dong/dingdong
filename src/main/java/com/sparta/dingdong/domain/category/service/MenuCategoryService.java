package com.sparta.dingdong.domain.category.service;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.domain.category.dto.MenuCategoryDto;

public interface MenuCategoryService {
	@Transactional(readOnly = true)
	BaseResponseDto<List<MenuCategoryDto.Response>> getByStore(UUID storeId);

	BaseResponseDto<MenuCategoryDto.Response> create(UUID storeId, MenuCategoryDto.Request req);

	BaseResponseDto<MenuCategoryDto.Response> update(UUID categoryId, MenuCategoryDto.Request req);

	BaseResponseDto<Void> delete(UUID categoryId);

	BaseResponseDto<MenuCategoryDto.Response> restore(UUID categoryId);
}
