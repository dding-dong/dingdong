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
import com.sparta.dingdong.domain.store.dto.StoreDto;
import com.sparta.dingdong.domain.store.entity.Store;
import com.sparta.dingdong.domain.store.entity.StoreDeliveryArea;
import com.sparta.dingdong.domain.store.entity.enums.StoreStatus;
import com.sparta.dingdong.domain.store.repository.StoreDeliveryAreaRepository;
import com.sparta.dingdong.domain.store.repository.StoreRepository;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.entity.enums.UserRole;
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
	public BaseResponseDto<List<StoreDto.Response>> getActiveStores() {
		List<StoreDto.Response> stores = storeRepository.findAllActive()
			.stream().map(this::mapPublic).toList();
		return BaseResponseDto.success("활성화된 가게 목록 조회 성공", stores);
	}

	@Transactional(readOnly = true)
	@Override
	public BaseResponseDto<List<StoreDto.Response>> getActiveStoresByCategory(UUID storeCategoryId) {
		List<StoreDto.Response> stores = storeRepository.findAllActiveByCategory(storeCategoryId)
			.stream().map(this::mapPublic).toList();
		return BaseResponseDto.success("활성화된 카테고리 가게 목록 조회 성공", stores);
	}

	/* ==================== OWNER 기능 ==================== */

	@Override
	public BaseResponseDto<StoreDto.Response> create(StoreDto.Request req) {
		UserAuth user = authService.getCurrentUser();
		User owner = userRepository.findById(user.getId())
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		StoreCategory category = storeCategoryRepository.findById(req.getCategoryId())
			.orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

		Store store = Store.builder()
			.name(req.getName())
			.description(req.getDescription())
			.address(req.getAddress())
			.postalCode(req.getPostalCode())
			.status(StoreStatus.CLOSED)
			.owner(owner)
			.category(category)
			.build();

		store = storeRepository.save(store);

		if (req.getDeliveryAreaIds() != null) {
			for (String dongId : req.getDeliveryAreaIds()) {
				addDeliveryArea(store.getId(), dongId);
			}
		}

		return BaseResponseDto.success("가게 생성 성공", map(store));
	}

	@Transactional(readOnly = true)
	@Override
	public BaseResponseDto<List<StoreDto.Response>> getMyStores() {
		UserAuth user = authService.getCurrentUser();
		List<StoreDto.Response> stores = storeRepository.findByOwnerId(user.getId())
			.stream().map(this::map).toList();
		return BaseResponseDto.success("내 가게 조회 성공", stores);
	}

	@Override
	public BaseResponseDto<StoreDto.Response> update(UUID id, StoreDto.Request req) {
		Store store = getStoreAndValidateOwnership(id);

		store.setName(req.getName());
		store.setDescription(req.getDescription());
		store.setAddress(req.getAddress());

		List<String> newAreaIds = req.getDeliveryAreaIds() != null ? req.getDeliveryAreaIds() : List.of();
		store.getDeliveryAreas().removeIf(area -> !newAreaIds.contains(area.getDong().getId()));

		for (String dongId : newAreaIds) {
			boolean exists = store.getDeliveryAreas().stream()
				.anyMatch(area -> area.getDong().getId().equals(dongId));
			if (!exists) {
				addDeliveryArea(store.getId(), dongId);
			}
		}

		return BaseResponseDto.success("가게 정보 수정 성공", map(store));
	}

	@Override
	public BaseResponseDto<StoreDto.Response> updateStatus(UUID id, StoreDto.UpdateStatusRequest req) {
		Store store = getStoreAndValidateOwnership(id);
		store.setStatus(req.getStatus());
		return BaseResponseDto.success("가게 상태 업데이트 성공", map(store));
	}

	@Override
	public BaseResponseDto<StoreDto.Response> getMyStore(UUID id) {
		Store store = getStoreAndValidateOwnership(id);
		return BaseResponseDto.success("내 가게 조회 성공", map(store));
	}

	@Override
	public BaseResponseDto<StoreDto.Response> addDeliveryArea(UUID storeId, String dongId) {
		Store store = getStoreAndValidateOwnership(storeId);

		Dong dong = dongRepository.findById(dongId)
			.orElseThrow(() -> new IllegalArgumentException("동 정보를 찾을 수 없습니다."));

		StoreDeliveryArea area = StoreDeliveryArea.builder()
			.store(store)
			.dong(dong)
			.build();

		store.getDeliveryAreas().add(area);
		storeDeliveryAreaRepository.save(area);

		return BaseResponseDto.success("배달 지역 추가 성공", map(store));
	}

	@Override
	public BaseResponseDto<StoreDto.Response> removeDeliveryArea(UUID storeId, UUID deliveryAreaId) {
		Store store = getStoreAndValidateOwnership(storeId);

		StoreDeliveryArea area = storeDeliveryAreaRepository.findById(deliveryAreaId)
			.orElseThrow(() -> new IllegalArgumentException("배달지역을 찾을 수 없습니다."));

		if (!area.getStore().getId().equals(storeId)) {
			throw new AccessDeniedException("본인 가게의 배달지역만 삭제 가능합니다.");
		}

		store.getDeliveryAreas().remove(area);
		storeDeliveryAreaRepository.delete(area);

		return BaseResponseDto.success("배달 지역 삭제 성공", map(store));
	}

	/* ==================== MANAGER/MASTER 기능 ==================== */

	@Transactional(readOnly = true)
	@Override
	public BaseResponseDto<List<StoreDto.Response>> getAll() {
		List<StoreDto.Response> stores = storeRepository.findAll().stream().map(this::map).toList();
		return BaseResponseDto.success("전체 가게 조회 성공", stores);
	}

	@Transactional(readOnly = true)
	@Override
	public BaseResponseDto<List<StoreDto.Response>> getAllByCategory(UUID storeCategoryId) {
		List<StoreDto.Response> stores = storeRepository.findAllByCategory(storeCategoryId)
			.stream().map(this::map).toList();
		return BaseResponseDto.success("카테고리별 가게 조회 성공", stores);
	}

	@Override
	public BaseResponseDto<StoreDto.Response> forceUpdateStatus(UUID id, StoreDto.UpdateStatusRequest req) {
		UserAuth user = authService.getCurrentUser();
		UserRole role = user.getUserRole();
		if (role != UserRole.MANAGER && role != UserRole.MASTER) {
			throw new AccessDeniedException("관리자 권한이 필요합니다.");
		}
		Store store = storeRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

		store.setStatus(req.getStatus() == StoreStatus.OPEN ? StoreStatus.FORCED_CLOSED : req.getStatus());
		return BaseResponseDto.success("가게 강제 상태 변경 성공", map(store));
	}

	@Override
	public BaseResponseDto<StoreDto.Response> manageUpdate(UUID id, StoreDto.@Valid Request req) {
		UserAuth user = authService.getCurrentUser();
		UserRole role = user.getUserRole();
		if (role != UserRole.MANAGER && role != UserRole.MASTER) {
			throw new AccessDeniedException("관리자 권한이 필요합니다.");
		}

		Store store = storeRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

		StoreCategory category = storeCategoryRepository.findById(req.getCategoryId())
			.orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

		store.setName(req.getName());
		store.setDescription(req.getDescription());
		store.setAddress(req.getAddress());
		store.setCategory(category);

		return BaseResponseDto.success("가게 정보 관리자 수정 성공", map(store));
	}

	@Override
	public BaseResponseDto<Void> delete(UUID id) {
		Store store = getStoreAndValidateOwnership(id);
		UserAuth user = authService.getCurrentUser();
		store.softDelete(user.getId());
		store.setStatus(StoreStatus.CLOSED);
		return BaseResponseDto.success("가게 삭제 성공");
	}

	@Override
	public BaseResponseDto<StoreDto.Response> getById(UUID id) {
		Store store = storeRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));
		return BaseResponseDto.success("가게 조회 성공", map(store));
	}

	@Override
	public BaseResponseDto<StoreDto.Response> restore(UUID storeId) {
		Store store = storeRepository.findDeletedById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("삭제된 가게를 찾을 수 없습니다: " + storeId));

		UserAuth user = authService.getCurrentUser();
		store.restore(user.getId());
		return BaseResponseDto.success("가게 복구 성공", map(store));
	}

	/* ==================== 유틸 메서드 ==================== */

	private Store getStoreAndValidateOwnership(UUID id) {
		Store store = storeRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));
		UserAuth user = authService.getCurrentUser();
		authService.validateStoreOwnership(user, store.getOwner().getId());
		return store;
	}

	private StoreDto.Response mapPublic(Store store) {
		return StoreDto.Response.builder()
			.id(store.getId())
			.name(store.getName())
			.description(store.getDescription())
			.address(store.getAddress())
			.postalCode(store.getPostalCode())
			.status(store.getStatus())
			.categoryId(store.getCategory().getId())
			.ownerId(store.getOwner().getId())
			.deliveryAreaIds(store.getDeliveryAreas().stream().map(d -> d.getDong().getId()).toList())
			.build();
	}

	private StoreDto.Response map(Store store) {
		UserAuth currentUser = authService.getCurrentUser();
		boolean isAdmin = currentUser.getUserRole() == UserRole.MANAGER
			|| currentUser.getUserRole() == UserRole.MASTER;

		StoreDto.Response.ResponseBuilder builder = StoreDto.Response.builder()
			.id(store.getId())
			.name(store.getName())
			.description(store.getDescription())
			.address(store.getAddress())
			.postalCode(store.getPostalCode())
			.status(store.getStatus())
			.categoryId(store.getCategory().getId())
			.ownerId(store.getOwner().getId())
			.deliveryAreaIds(store.getDeliveryAreas().stream().map(d -> d.getDong().getId()).toList());

		if (isAdmin) {
			builder
				.createdAt(store.getCreatedAt())
				.createdBy(store.getCreatedBy())
				.updatedAt(store.getUpdatedAt())
				.updatedBy(store.getUpdatedBy())
				.deletedAt(store.getDeletedAt())
				.deletedBy(store.getDeletedBy());
		}

		return builder.build();
	}
}