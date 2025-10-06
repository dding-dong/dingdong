package com.sparta.dingdong.domain.store.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	/* ==================== PUBLIC 기능 (로그인 없이 접근 가능) ==================== */

	@Transactional(readOnly = true)
	@Override
	public List<StoreDto.Response> getActiveStores() {
		return storeRepository.findAllActive()
			.stream()
			.map(this::mapPublic)
			.toList();
	}

	@Transactional(readOnly = true)
	@Override
	public List<StoreDto.Response> getActiveStoresByCategory(UUID storeCategoryId) {
		return storeRepository.findAllActiveByCategory(storeCategoryId)
			.stream()
			.map(this::mapPublic)
			.toList();
	}

	/* ==================== OWNER 기능 ==================== */

	@Override
	public StoreDto.Response create(StoreDto.Request req) {
		UserAuth userAuth = authService.getCurrentUser();
		User owner = userRepository.findById(userAuth.getId())
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

		return map(store);
	}

	@Override
	@Transactional(readOnly = true)
	public List<StoreDto.Response> getMyStores() {
		UserAuth user = authService.getCurrentUser();
		return storeRepository.findByOwnerId(user.getId())
			.stream().map(this::map).toList();
	}

	@Override
	public StoreDto.Response update(UUID id, StoreDto.Request req) {
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

		return map(store);
	}

	@Override
	public StoreDto.Response updateStatus(UUID id, StoreDto.UpdateStatusRequest req) {
		Store store = getStoreAndValidateOwnership(id);
		store.setStatus(req.getStatus());
		return map(store);
	}

	@Override
	public StoreDto.Response getMyStore(UUID id) {
		Store store = getStoreAndValidateOwnership(id);
		return map(store);
	}

	@Override
	public StoreDto.Response addDeliveryArea(UUID storeId, String dongId) {
		Store store = getStoreAndValidateOwnership(storeId);

		Dong dong = dongRepository.findById(dongId)
			.orElseThrow(() -> new IllegalArgumentException("동 정보를 찾을 수 없습니다."));

		StoreDeliveryArea area = StoreDeliveryArea.builder()
			.store(store)
			.dong(dong)
			.build();

		store.getDeliveryAreas().add(area);
		storeDeliveryAreaRepository.save(area);

		return map(store);
	}

	@Override
	public StoreDto.Response removeDeliveryArea(UUID storeId, UUID deliveryAreaId) {
		Store store = getStoreAndValidateOwnership(storeId);

		StoreDeliveryArea area = storeDeliveryAreaRepository.findById(deliveryAreaId)
			.orElseThrow(() -> new IllegalArgumentException("배달지역을 찾을 수 없습니다."));

		if (!area.getStore().getId().equals(storeId)) {
			throw new AccessDeniedException("본인 가게의 배달지역만 삭제 가능합니다.");
		}

		store.getDeliveryAreas().remove(area);
		storeDeliveryAreaRepository.delete(area);

		return map(store);
	}

	/* ==================== MANAGER/MASTER 기능 ==================== */

	@Transactional(readOnly = true)
	@Override
	public List<StoreDto.Response> getAll() {
		return storeRepository.findAll()
			.stream()
			.map(this::map)
			.toList();
	}

	@Transactional(readOnly = true)
	@Override
	public List<StoreDto.Response> getAllByCategory(UUID storeCategoryId) {
		return storeRepository.findAllByCategory(storeCategoryId)
			.stream()
			.map(this::map)
			.toList();
	}

	@Override
	public StoreDto.Response forceUpdateStatus(UUID id, StoreDto.UpdateStatusRequest req) {
		UserAuth user = authService.getCurrentUser(); // ✅
		UserRole role = user.getUserRole();
		if (role != UserRole.MANAGER && role != UserRole.MASTER) {
			throw new AccessDeniedException("관리자 권한이 필요합니다.");
		}
		Store store = storeRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

		store.setStatus(req.getStatus() == StoreStatus.OPEN ? StoreStatus.FORCED_CLOSED : req.getStatus());
		return map(store);
	}

	@Override
	public StoreDto.Response manageUpdate(UUID id, StoreDto.@Valid Request req) {
		UserAuth user = authService.getCurrentUser(); // ✅
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

		return map(store);
	}

	@Override
	public void delete(UUID id) {
		Store store = getStoreAndValidateOwnership(id);
		UserAuth user = authService.getCurrentUser();
		store.softDelete(user.getId());
		store.setStatus(StoreStatus.CLOSED);
	}

	@Override
	public StoreDto.Response getById(UUID id) {
		Store store = storeRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));
		return map(store);
	}

	@Override
	public StoreDto.Response restore(UUID storeId) {
		Store store = storeRepository.findDeletedById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("삭제된 가게를 찾을 수 없습니다: " + storeId));

		UserAuth user = authService.getCurrentUser();
		store.restore(user.getId());
		return map(store);
	}

	/* ==================== 유틸 메서드 ==================== */

	/** OWNER: 본인 가게만 접근 가능 / MANAGER, MASTER: 전체 접근 가능 */
	private Store getStoreAndValidateOwnership(UUID id) {
		Store store = storeRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

		UserAuth user = authService.getCurrentUser(); // ✅ 변경
		authService.validateStoreOwnership(user, store.getOwner().getId()); // ✅ 변경

		return store;
	}

	/** 로그인 여부 안전 map() - public 접근용 */
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
			.deliveryAreaIds(
				store.getDeliveryAreas().stream()
					.map(d -> d.getDong().getId())
					.toList()
			)
			.build();
	}

	/** 로그인 필수 map() - OWNER/ADMIN 등 인증 필요용 */
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
			.deliveryAreaIds(
				store.getDeliveryAreas().stream()
					.map(d -> d.getDong().getId())
					.toList()
			);

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
