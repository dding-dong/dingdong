package com.sparta.dingdong.domain.menu.service;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.menu.dto.request.MenuItemRequestDto;
import com.sparta.dingdong.domain.menu.dto.response.MenuItemResponseDto;

public interface MenuItemService {
	@Transactional(readOnly = true)
	List<MenuItemResponseDto> getAllByStore(UUID storeId, boolean includeHidden, UserAuth user);

	MenuItemResponseDto create(UUID storeId, MenuItemRequestDto req, UserAuth user);

	@Transactional(readOnly = true)
	MenuItemResponseDto getById(UUID menuId, UserAuth user);

	MenuItemResponseDto update(UUID menuId, MenuItemRequestDto req, UserAuth user);

	void delete(UUID menuId, UserAuth user);
}
