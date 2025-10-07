package com.sparta.dingdong.domain.category.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.category.dto.request.StoreCategoryRequestDto;
import com.sparta.dingdong.domain.category.dto.response.StoreCategoryResponseDto;
import com.sparta.dingdong.domain.category.entity.StoreCategory;
import com.sparta.dingdong.domain.category.repository.StoreCategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StoreCategoryServiceImpl implements StoreCategoryService {

	private final StoreCategoryRepository storeCategoryRepository;

	@Transactional(readOnly = true)
	@Override
	public List<StoreCategoryResponseDto> getAll() {
		return storeCategoryRepository.findAllByDeletedAtIsNull().stream()
			.map(this::map)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	@Override
	public StoreCategoryResponseDto getById(UUID id) {
		StoreCategory sc = getCategoryOrThrow(id);
		return map(sc);
	}

	@Override
	public StoreCategoryResponseDto create(StoreCategoryRequestDto req) {
		StoreCategory sc = StoreCategory.builder()
			.name(req.getName())
			.description(req.getDescription())
			.imageUrl(req.getImageUrl())
			.build();
		StoreCategory saved = storeCategoryRepository.save(sc);
		return map(saved);
	}

	@Override
	public StoreCategoryResponseDto update(UUID id, StoreCategoryRequestDto req) {
		StoreCategory sc = getCategoryOrThrow(id);
		sc.setName(req.getName());
		sc.setDescription(req.getDescription());
		sc.setImageUrl(req.getImageUrl());
		return map(sc);
	}

	@Override
	public void delete(UUID id, UserAuth user) {
		StoreCategory sc = getCategoryOrThrow(id);
		sc.softDelete(user.getId());
		storeCategoryRepository.save(sc);
	}

	@Override
	public StoreCategoryResponseDto restore(UUID id, UserAuth user) {
		StoreCategory sc = getCategoryOrThrow(id);
		if (!sc.isDeleted()) {
			throw new IllegalStateException("이미 활성화된 카테고리입니다.");
		}
		sc.restore(user.getId());
		storeCategoryRepository.save(sc);
		return map(sc);
	}

	/* ==================== 유틸 메서드 ==================== */

	private StoreCategory getCategoryOrThrow(UUID id) {
		return storeCategoryRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("가게 카테고리를 찾을 수 없습니다: " + id));
	}

	private StoreCategoryResponseDto map(StoreCategory sc) {
		return StoreCategoryResponseDto.builder()
			.id(sc.getId())
			.name(sc.getName())
			.description(sc.getDescription())
			.imageUrl(sc.getImageUrl())
			.build();
	}
}
