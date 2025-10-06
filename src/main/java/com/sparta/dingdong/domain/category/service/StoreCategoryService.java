package com.sparta.dingdong.domain.category.service;

import java.util.List;
import java.util.UUID;

import com.sparta.dingdong.domain.category.dto.StoreCategoryDto;

public interface StoreCategoryService {
	List<StoreCategoryDto.Response> getAll();

	StoreCategoryDto.Response getById(UUID id);

	StoreCategoryDto.Response create(StoreCategoryDto.Request req);

	StoreCategoryDto.Response update(UUID id, StoreCategoryDto.Request req);

	void delete(UUID id);

	StoreCategoryDto.Response restore(UUID id); // 소프트 딜리트 복구 기능
}
