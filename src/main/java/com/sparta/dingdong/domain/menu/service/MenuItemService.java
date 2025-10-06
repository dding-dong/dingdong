package com.sparta.dingdong.domain.menu.service;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.domain.menu.dto.MenuItemDto;

public interface MenuItemService {
	@Transactional(readOnly = true)
	List<MenuItemDto.Response> getAllByStore(UUID storeId, boolean includeHidden);
	
	MenuItemDto.Response create(UUID storeId, MenuItemDto.Request req);

	@Transactional(readOnly = true)
	MenuItemDto.Response getById(UUID menuId);

	MenuItemDto.Response update(UUID menuId, MenuItemDto.Request req);

	void delete(UUID menuId);
}
