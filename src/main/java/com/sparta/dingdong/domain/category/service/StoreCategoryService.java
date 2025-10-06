package com.sparta.dingdong.domain.category.service;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.domain.category.dto.StoreCategoryDto;

public interface StoreCategoryService {

	@Transactional(readOnly = true)
	BaseResponseDto<List<StoreCategoryDto.Response>> getAll();

	BaseResponseDto<StoreCategoryDto.Response> getById(UUID id);

	BaseResponseDto<StoreCategoryDto.Response> create(StoreCategoryDto.Request req);

	BaseResponseDto<StoreCategoryDto.Response> update(UUID id, StoreCategoryDto.Request req);

	BaseResponseDto<Void> delete(UUID id);

	BaseResponseDto<StoreCategoryDto.Response> restore(UUID id);
}
