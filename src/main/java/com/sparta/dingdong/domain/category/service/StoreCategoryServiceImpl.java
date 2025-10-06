package com.sparta.dingdong.domain.category.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.dto.BaseResponseDto;
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

	@Transactional(readOnly = true)
	@Override
	public BaseResponseDto<List<StoreCategoryDto.Response>> getAll() {
		List<StoreCategoryDto.Response> storeCategories = storeCategoryRepository.findAllByDeletedAtIsNull().stream()
			.map(this::map)
			.collect(Collectors.toList());
		return BaseResponseDto.success("가게 카테고리 목록 조회 성공", storeCategories);
	}

	@Override
	public BaseResponseDto<StoreCategoryDto.Response> getById(UUID id) {
		StoreCategory sc = storeCategoryRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new IllegalArgumentException("삭제되었거나 존재하지 않는 카테고리입니다: " + id));
		return BaseResponseDto.success("가게 카테고리 조회 성공", map(sc));
	}

	@Override
	public BaseResponseDto<StoreCategoryDto.Response> create(StoreCategoryDto.Request req) {
		StoreCategory sc = StoreCategory.builder()
			.name(req.getName())
			.description(req.getDescription())
			.imageUrl(req.getImageUrl())
			.build();
		StoreCategory saved = storeCategoryRepository.save(sc);
		return BaseResponseDto.success("가게 카테고리 생성 성공", map(saved));
	}

	@Override
	public BaseResponseDto<StoreCategoryDto.Response> update(UUID id, StoreCategoryDto.Request req) {
		StoreCategory sc = storeCategoryRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("가게 카테고리를 찾을 수 없습니다: " + id));

		sc.setName(req.getName());
		sc.setDescription(req.getDescription());
		sc.setImageUrl(req.getImageUrl());

		return BaseResponseDto.success("가게 카테고리 수정 성공", map(sc));
	}

	@Override
	public BaseResponseDto<Void> delete(UUID id) {
		StoreCategory sc = storeCategoryRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("가게 카테고리를 찾을 수 없습니다: " + id));
		UserAuth user = authService.getCurrentUser();
		sc.softDelete(user.getId());
		storeCategoryRepository.save(sc);
		return BaseResponseDto.success("가게 카테고리 삭제 성공");
	}

	@Override
	public BaseResponseDto<StoreCategoryDto.Response> restore(UUID id) {
		StoreCategory sc = storeCategoryRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("가게 카테고리를 찾을 수 없습니다: " + id));

		if (!sc.isDeleted()) {
			throw new IllegalStateException("이미 활성화된 카테고리입니다.");
		}
		UserAuth user = authService.getCurrentUser();
		sc.restore(user.getId());
		return BaseResponseDto.success("가게 카테고리 복구 성공", map(sc));
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