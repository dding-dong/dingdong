package com.sparta.dingdong.domain.category.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.dingdong.domain.category.entity.StoreCategory;

public interface StoreCategoryRepositoryCustom {
	Page<StoreCategory> findAllByDeletedAtIsNullWithKeyword(String keyword, Pageable pageable);
}
