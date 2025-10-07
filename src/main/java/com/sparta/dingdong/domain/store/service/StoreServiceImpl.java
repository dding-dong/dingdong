package com.sparta.dingdong.domain.store.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.entity.Dong;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.auth.service.AuthService;
import com.sparta.dingdong.domain.category.entity.StoreCategory;
import com.sparta.dingdong.domain.category.repository.StoreCategoryRepository;
import com.sparta.dingdong.domain.store.dto.request.StoreRequestDto;
import com.sparta.dingdong.domain.store.dto.request.StoreUpdateStatusRequestDto;
import com.sparta.dingdong.domain.store.dto.response.StoreResponseDto;
import com.sparta.dingdong.domain.store.entity.Store;
import com.sparta.dingdong.domain.store.entity.StoreDeliveryArea;
import com.sparta.dingdong.domain.store.entity.enums.StoreStatus;
import com.sparta.dingdong.domain.store.repository.StoreDeliveryAreaRepository;
import com.sparta.dingdong.domain.store.repository.StoreRepository;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.repository.DongRepository;
import com.sparta.dingdong.domain.user.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StoreServiceImpl implements StoreService {

	private final StoreRepository storeRepository;
	private final StoreCategoryRepository storeCategoryRepository;
	private final StoreDeliveryAreaRepository storeDeliveryAreaRepository;
	private final UserRepository userRepository;
	private final DongRepository dongRepository;
	private final AuthService authService;

	/* ==================== PUBLIC 기능 ==================== */

	@Transactional(readOnly = true)
	@Override
	public BaseResponseDto<List<StoreResponseDto>> getActiveStores(UserAuth user) {
		List<StoreResponseDto> stores = storeRepository.findAllActive()
			.stream().map(this::mapPublic).toList();
		log.info(user != null && user.getId() != null ? "로그인 사용자가 가게 조회" : "비회원이 가게 조회");
		return BaseResponseDto.success("활성화된 가게 목록 조회 성공", stores);
	}

	@Transactional(readOnly = true)
	@Override
	public BaseResponseDto<List<StoreResponseDto>> getActiveStoresByCategory(UUID storeCategoryId, UserAuth user) {
		List<StoreResponseDto> stores = storeRepository.findAllActiveByStoreCategory(storeCategoryId)
			.stream().map(this::mapPublic).toList();
		log.info(user != null && user.getId() != null ? "로그인 사용자가 가게 조회" : "비회원이 가게 조회");
		return BaseResponseDto.success("활성화된 카테고리 가게 목록 조회 성공", stores);
	}

	/* ==================== OWNER 기능 ==================== */

	@Override
	public BaseResponseDto<StoreResponseDto> create(StoreRequestDto req, UserAuth user) {
		boolean isAdmin = authService.isAdmin(user);
		User currentUser = userRepository.findById(user.getId())
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		StoreCategory storeCategory = getCategoryOrThrow(req.getStoreCategoryId());

		Store store = Store.builder()
			.name(req.getName())
			.description(req.getDescription())
			.address(req.getAddress())
			.postalCode(req.getPostalCode())
			.status(StoreStatus.CLOSED)
			.owner(currentUser)
			.storeCategory(storeCategory)
			.build();

		store = storeRepository.save(store);
		if (req.getDeliveryAreaIds() != null) {
			for (String dongId : req.getDeliveryAreaIds()) {
				addDeliveryArea(store.getId(), dongId, user);
			}
		}

		return BaseResponseDto.success("가게 생성 성공", map(store, isAdmin));
	}

	@Transactional(readOnly = true)
	@Override
	public BaseResponseDto<List<StoreResponseDto>> getMyStores(UserAuth user) {
		boolean isAdmin = authService.isAdmin(user);
		List<StoreResponseDto> stores = storeRepository.findByOwnerId(user.getId())
			.stream().map(store -> map(store, isAdmin)).toList();
		return BaseResponseDto.success("내 가게 조회 성공", stores);
	}

	@Override
	public BaseResponseDto<StoreResponseDto> update(UUID storeId, StoreRequestDto req, UserAuth user) {
		boolean isAdmin = authService.isAdmin(user);
		Store store = getStoreOrThrow(storeId);
		authService.validateStoreOwnership(user, store.getOwner().getId());

		store.setName(req.getName());
		store.setDescription(req.getDescription());
		store.setAddress(req.getAddress());

		List<String> newAreaIds = req.getDeliveryAreaIds() != null ? req.getDeliveryAreaIds() : List.of();
		store.getDeliveryAreas().removeIf(area -> !newAreaIds.contains(area.getDong().getId()));
		for (String dongId : newAreaIds) {
			boolean exists = store.getDeliveryAreas().stream()
				.anyMatch(area -> area.getDong().getId().equals(dongId));
			if (!exists) {
				addDeliveryArea(store.getId(), dongId, user);
			}
		}

		return BaseResponseDto.success("가게 정보 수정 성공", map(store, isAdmin));
	}

	@Override
	public BaseResponseDto<StoreResponseDto> updateStatus(UUID storeId, StoreUpdateStatusRequestDto req,
		UserAuth user) {
		boolean isAdmin = authService.isAdmin(user);
		Store store = getStoreOrThrow(storeId);
		authService.validateStoreOwnership(user, store.getOwner().getId());
		store.setStatus(req.getStatus());
		return BaseResponseDto.success("가게 상태 업데이트 성공", map(store, isAdmin));
	}

	@Override
	public BaseResponseDto<StoreResponseDto> getMyStore(UUID storeId, UserAuth user) {
		boolean isAdmin = authService.isAdmin(user);
		Store store = getStoreOrThrow(storeId);
		authService.validateStoreOwnership(user, store.getOwner().getId());
		return BaseResponseDto.success("내 가게 조회 성공", map(store, isAdmin));
	}

	@Override
	public BaseResponseDto<StoreResponseDto> addDeliveryArea(UUID storeId, String dongId, UserAuth user) {
		boolean isAdmin = authService.isAdmin(user);
		Store store = getStoreOrThrow(storeId);

		Dong dong = dongRepository.findById(dongId)
			.orElseThrow(() -> new IllegalArgumentException("동 정보를 찾을 수 없습니다."));
		StoreDeliveryArea area = StoreDeliveryArea.builder()
			.store(store)
			.dong(dong)
			.build();
		store.getDeliveryAreas().add(area);

		storeDeliveryAreaRepository.save(area);
		return BaseResponseDto.success("배달 지역 추가 성공", map(store, isAdmin));
	}

	@Override
	public BaseResponseDto<StoreResponseDto> removeDeliveryArea(UUID storeId, UUID deliveryAreaId, UserAuth user) {
		boolean isAdmin = authService.isAdmin(user);
		Store store = getStoreOrThrow(storeId);
		authService.validateStoreOwnership(user, store.getOwner().getId());

		StoreDeliveryArea area = storeDeliveryAreaRepository.findById(deliveryAreaId)
			.orElseThrow(() -> new IllegalArgumentException("배달지역을 찾을 수 없습니다."));
		if (!area.getStore().getId().equals(storeId)) {
			throw new AccessDeniedException("본인 가게의 배달지역만 삭제 가능합니다.");
		}
		store.getDeliveryAreas().remove(area);

		storeDeliveryAreaRepository.delete(area);
		return BaseResponseDto.success("배달 지역 삭제 성공", map(store, isAdmin));
	}

	/* ==================== MANAGER/MASTER 기능 ==================== */

	@Transactional(readOnly = true)
	@Override
	public BaseResponseDto<List<StoreResponseDto>> getAll(UserAuth user) {
		authService.ensureAdmin(user);
		boolean isAdmin = true;
		List<StoreResponseDto> stores = storeRepository.findAll()
			.stream().map(store -> map(store, isAdmin)).toList();
		return BaseResponseDto.success("전체 가게 조회 성공", stores);
	}

	@Transactional(readOnly = true)
	@Override
	public BaseResponseDto<List<StoreResponseDto>> getAllByCategory(UUID storeCategoryId, UserAuth user) {
		authService.ensureAdmin(user);
		boolean isAdmin = true;
		List<StoreResponseDto> stores = storeRepository.findAllByStoreCategory(storeCategoryId)
			.stream().map(store -> map(store, isAdmin)).toList();
		return BaseResponseDto.success("카테고리별 가게 조회 성공", stores);
	}

	@Override
	public BaseResponseDto<StoreResponseDto> forceUpdateStatus(UUID storeId, StoreUpdateStatusRequestDto req,
		UserAuth user) {
		authService.ensureAdmin(user);
		boolean isAdmin = true;
		Store store = getStoreOrThrow(storeId);
		store.setStatus(req.getStatus());
		return BaseResponseDto.success("가게 강제 상태 변경 성공", map(store, isAdmin));
	}

	@Override
	public BaseResponseDto<StoreResponseDto> manageUpdate(UUID storeId, @Valid StoreRequestDto req, UserAuth user) {
		authService.ensureAdmin(user);
		boolean isAdmin = true;
		Store store = getStoreOrThrow(storeId);
		StoreCategory storeCategory = getCategoryOrThrow(req.getStoreCategoryId());
		store.setName(req.getName());
		store.setDescription(req.getDescription());
		store.setAddress(req.getAddress());
		store.setStoreCategory(storeCategory);
		return BaseResponseDto.success("가게 정보 관리자 수정 성공", map(store, isAdmin));
	}

	@Override
	public BaseResponseDto<Void> delete(UUID storeId, UserAuth user) {
		authService.ensureAdmin(user);
		boolean isAdmin = true;
		Store store = getStoreOrThrow(storeId);
		store.softDelete(user.getId());
		store.setStatus(StoreStatus.CLOSED);
		return BaseResponseDto.success("가게 삭제 성공");
	}

	@Override
	public BaseResponseDto<StoreResponseDto> getById(UUID storeId, UserAuth user) {
		authService.ensureAdmin(user);
		boolean isAdmin = true;
		Store store = getStoreOrThrow(storeId);
		return BaseResponseDto.success("가게 조회 성공", map(store, isAdmin));
	}

	@Override
	public BaseResponseDto<StoreResponseDto> restore(UUID storeId, UserAuth user) {
		authService.ensureAdmin(user);
		boolean isAdmin = true;
		Store store = getDeletedStoreOrThrow(storeId);
		store.restore(user.getId());
		return BaseResponseDto.success("가게 복구 성공", map(store, isAdmin));
	}

	/* ==================== 유틸 메서드 ==================== */

	/**
	 * ID로 가게 조회, 없으면 예외 발생
	 */
	private Store getStoreOrThrow(UUID storeId) {
		return storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다: " + storeId));
	}

	/**
	 * ID로 가게 카테고리 조회, 없으면 예외 발생
	 */
	private StoreCategory getCategoryOrThrow(UUID categoryId) {
		return storeCategoryRepository.findById(categoryId)
			.orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + categoryId));
	}

	/**
	 * 삭제된 가게 조회, 없으면 예외 발생
	 */
	private Store getDeletedStoreOrThrow(UUID storeId) {
		return storeRepository.findDeletedById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("삭제된 가게를 찾을 수 없습니다: " + storeId));
	}

	private StoreResponseDto map(Store store, boolean isAdmin) {
		return isAdmin ? mapAdmin(store) : mapPublic(store);
	}

	private StoreResponseDto mapPublic(Store store) {
		return StoreResponseDto.builder()
			.id(store.getId())
			.name(store.getName())
			.description(store.getDescription())
			.address(store.getAddress())
			.postalCode(store.getPostalCode())
			.status(store.getStatus())
			.storeCategoryId(store.getStoreCategory().getId())
			.ownerId(store.getOwner().getId())
			.deliveryAreaIds(store.getDeliveryAreas().stream()
				.map(d -> d.getDong().getId())
				.toList())
			.build();
	}

	private StoreResponseDto mapAdmin(Store store) {
		return StoreResponseDto.builder()
			.id(store.getId())
			.name(store.getName())
			.description(store.getDescription())
			.address(store.getAddress())
			.postalCode(store.getPostalCode())
			.status(store.getStatus())
			.storeCategoryId(store.getStoreCategory().getId())
			.ownerId(store.getOwner().getId())
			.deliveryAreaIds(store.getDeliveryAreas().stream()
				.map(d -> d.getDong().getId())
				.toList())
			.createdAt(store.getCreatedAt())
			.createdBy(store.getCreatedBy())
			.updatedAt(store.getUpdatedAt())
			.updatedBy(store.getUpdatedBy())
			.deletedAt(store.getDeletedAt())
			.deletedBy(store.getDeletedBy())
			.build();
	}
}
