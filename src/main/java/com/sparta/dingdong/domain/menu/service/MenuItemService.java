package com.sparta.dingdong.domain.menu.service;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.domain.menu.dto.MenuItemDto;

public interface MenuItemService {
	@Transactional(readOnly = true)
	BaseResponseDto<List<MenuItemDto.Response>> getAllByStore(UUID storeId, boolean includeHidden);

	BaseResponseDto<MenuItemDto.Response> create(UUID storeId, MenuItemDto.Request req);

	@Transactional(readOnly = true)
	BaseResponseDto<MenuItemDto.Response> getById(UUID menuId);

	BaseResponseDto<MenuItemDto.Response> update(UUID menuId, MenuItemDto.Request req);

	BaseResponseDto<Void> delete(UUID menuId);
}
