package com.sparta.dingdong.domain.category.service;

import java.util.List;
import java.util.UUID;

import com.sparta.dingdong.domain.category.dto.MenuCategoryDto;

public interface MenuCategoryService {
	List<MenuCategoryDto.Response> getByStore(UUID storeId);

	MenuCategoryDto.Response create(UUID storeId, MenuCategoryDto.Request req);

	MenuCategoryDto.Response update(UUID categoryId, MenuCategoryDto.Request req);

	void delete(UUID categoryId);

	MenuCategoryDto.Response restore(UUID categoryId);

}
