package com.sparta.dingdong.domain.category.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.auth.service.AuthService;
import com.sparta.dingdong.domain.category.dto.StoreCategoryDto;
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
	private final AuthService authService;

	@Override
	@Transactional(readOnly = true)
	public List<StoreCategoryDto.Response> getAll() {
		return storeCategoryRepository.findAllByDeletedAtIsNull().stream()
			.map(this::map)
			.collect(Collectors.toList());
	}

	@Override
	public StoreCategoryDto.Response getById(UUID id) {
		StoreCategory sc = storeCategoryRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new IllegalArgumentException("삭제되었거나 존재하지 않는 카테고리입니다: " + id));
		return map(sc);
	}

	@Override
	public StoreCategoryDto.Response create(StoreCategoryDto.Request req) {
		StoreCategory entity = StoreCategory.builder()
			.name(req.getName())
			.description(req.getDescription())
			.imageUrl(req.getImageUrl())
			.build();
		StoreCategory saved = storeCategoryRepository.save(entity);
		return map(saved);
	}

	@Override
	public StoreCategoryDto.Response update(UUID id, StoreCategoryDto.Request req) {
		StoreCategory sc = storeCategoryRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("가게 카테고리를 찾을 수 없습니다: " + id));

		sc.setName(req.getName());
		sc.setDescription(req.getDescription());
		sc.setImageUrl(req.getImageUrl());

		return map(sc);
	}

	@Override
	public void delete(UUID id) {
		StoreCategory sc = storeCategoryRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("가게 카테고리를 찾을 수 없습니다: " + id));
		UserAuth user = authService.getCurrentUser();
		sc.softDelete(user.getId());
		storeCategoryRepository.save(sc);
	}

	@Override
	public StoreCategoryDto.Response restore(UUID id) {
		// 소프트 삭제된 카테고리만 조회
		StoreCategory sc = storeCategoryRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("가게 카테고리를 찾을 수 없습니다: " + id));
		if (!sc.isDeleted()) {
			throw new IllegalStateException("이미 활성화된 카테고리입니다.");
		}
		UserAuth user = authService.getCurrentUser();
		sc.restore(user.getId());
		return map(sc);
	}

	/* ==================== 유틸 메서드 ==================== */

	private StoreCategoryDto.Response map(StoreCategory sc) {
		return StoreCategoryDto.Response.builder()
			.id(sc.getId())
			.name(sc.getName())
			.description(sc.getDescription())
			.imageUrl(sc.getImageUrl())
			.build();
	}
}