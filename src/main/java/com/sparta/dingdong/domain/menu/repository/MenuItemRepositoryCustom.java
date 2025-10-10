package com.sparta.dingdong.domain.menu.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.dingdong.domain.menu.entity.MenuItem;

public interface MenuItemRepositoryCustom {

	Page<MenuItem> findByStoreIdWithKeyword(UUID storeId, String keyword, Pageable pageable,
		boolean includeDeleted);
	
}
