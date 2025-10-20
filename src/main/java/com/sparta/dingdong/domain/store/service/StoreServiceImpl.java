package com.sparta.dingdong.domain.store.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.entity.Dong;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.auth.service.AuthService;
import com.sparta.dingdong.domain.category.entity.StoreCategory;
import com.sparta.dingdong.domain.category.exception.storecategory.StoreCategoryNotFoundException;
import com.sparta.dingdong.domain.category.repository.storecategory.StoreCategoryRepository;
import com.sparta.dingdong.domain.store.dto.request.StoreRequestDto;
import com.sparta.dingdong.domain.store.dto.request.StoreUpdateStatusRequestDto;
import com.sparta.dingdong.domain.store.dto.response.StoreResponseDto;
import com.sparta.dingdong.domain.store.entity.Store;
import com.sparta.dingdong.domain.store.entity.StoreDeliveryArea;
import com.sparta.dingdong.domain.store.entity.enums.StoreStatus;
import com.sparta.dingdong.domain.store.exception.DeletedStoreNotFoundException;
import com.sparta.dingdong.domain.store.exception.DeliveryAreaNotFoundException;
import com.sparta.dingdong.domain.store.exception.NotStoreOwnerException;
import com.sparta.dingdong.domain.store.exception.StoreNotFoundException;
import com.sparta.dingdong.domain.store.repository.StoreDeliveryAreaRepository;
import com.sparta.dingdong.domain.store.repository.StoreRepository;
import com.sparta.dingdong.domain.user.entity.Address;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.entity.enums.UserRole;
import com.sparta.dingdong.domain.user.exception.UserNotFoundException;
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
	public Page<StoreResponseDto> getActiveStores(String keyword, Pageable pageable, UserAuth user) {
		List<String> userDongIds = null;
		if (user != null && user.getUserRole() == UserRole.CUSTOMER) {
			User customer = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException());
			userDongIds = customer.getAddressList().stream()
				.filter(Address::isDefault)
				.map(addr -> addr.getDong().getId())
				.filter(Objects::nonNull)
				.toList();
		}
		Page<Store> storesPage = storeRepository.findAllActiveWithKeyword(keyword, pageable, userDongIds);
		log.info(user != null && user.getId() != null ? "로그인 사용자가 가게 조회" : "비회원이 가게 조회");
		return storesPage.map(this::mapPublic);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<StoreResponseDto> getActiveStoresByCategory(UUID storeCategoryId, String keyword, Pageable pageable,
		UserAuth user) {
		List<String> userDongIds = null;
		if (user != null && user.getUserRole() == UserRole.CUSTOMER) {
			User customer = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException());
			userDongIds = customer.getAddressList().stream()
				.filter(Address::isDefault)
				.map(addr -> addr.getDong().getId())
				.filter(Objects::nonNull)
				.toList();
		}
		Page<Store> storesPage = storeRepository.findAllActiveByStoreCategoryWithKeyword(storeCategoryId, keyword,
			pageable, userDongIds);
		log.info(user != null && user.getId() != null ? "로그인 사용자가 가게 조회" : "비회원이 가게 조회");
		return storesPage.map(this::mapPublic);
	}

	/* ==================== OWNER 기능 ==================== */

	@Override
	public StoreResponseDto create(StoreRequestDto req, UserAuth user) {
		boolean isAdmin = authService.isAdmin(user);
		User currentUser = userRepository.findById(user.getId())
			.orElseThrow(() -> new UserNotFoundException());
		StoreCategory storeCategory = getStoreCategoryOrThrow(req.getStoreCategoryId());

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

		return map(store, isAdmin);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<StoreResponseDto> getMyStores(String keyword, Pageable pageable, UserAuth user) {
		boolean isAdmin = authService.isAdmin(user);
		Page<Store> storesPage = storeRepository.findByOwnerIdWithKeyword(user.getId(), keyword, pageable);
		return storesPage.map(store -> map(store, isAdmin));
	}

	@Override
	public StoreResponseDto update(UUID storeId, StoreRequestDto req, UserAuth user) {
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

		return map(store, isAdmin);
	}

	@Override
	public StoreResponseDto updateStatus(UUID storeId, StoreUpdateStatusRequestDto req, UserAuth user) {
		boolean isAdmin = authService.isAdmin(user);
		Store store = getStoreOrThrow(storeId);
		authService.validateStoreOwnership(user, store.getOwner().getId());
		store.setStatus(req.getStatus());
		return map(store, isAdmin);
	}

	@Override
	public StoreResponseDto getMyStore(UUID storeId, UserAuth user) {
		boolean isAdmin = authService.isAdmin(user);
		Store store = getStoreOrThrow(storeId);
		authService.validateStoreOwnership(user, store.getOwner().getId());
		return map(store, isAdmin);
	}

	@Override
	public StoreResponseDto addDeliveryArea(UUID storeId, String dongId, UserAuth user) {
		boolean isAdmin = authService.isAdmin(user);
		Store store = getStoreOrThrow(storeId);

		Dong dong = dongRepository.findByIdOrElseThrow(dongId);

		StoreDeliveryArea area = StoreDeliveryArea.builder()
			.store(store)
			.dong(dong)
			.build();

		store.getDeliveryAreas().add(area);
		storeDeliveryAreaRepository.save(area);

		return map(store, isAdmin);
	}

	@Override
	public StoreResponseDto removeDeliveryArea(UUID storeId, UUID deliveryAreaId, UserAuth user) {
		boolean isAdmin = authService.isAdmin(user);
		Store store = getStoreOrThrow(storeId);
		authService.validateStoreOwnership(user, store.getOwner().getId());

		StoreDeliveryArea area = storeDeliveryAreaRepository.findById(deliveryAreaId)
			.orElseThrow(DeliveryAreaNotFoundException::new);
		if (!area.getStore().getId().equals(storeId)) {
			throw new NotStoreOwnerException("본인 가게의 배달지역만 삭제 가능합니다.");
		}

		store.getDeliveryAreas().remove(area);
		storeDeliveryAreaRepository.delete(area);

		return map(store, isAdmin);
	}

	/* ==================== MANAGER/MASTER 기능 ==================== */

	@Transactional(readOnly = true)
	@Override
	public Page<StoreResponseDto> getAllStores(String keyword, Pageable pageable, UserAuth user) {
		authService.ensureAdmin(user);
		boolean isAdmin = true;
		Page<Store> storesPage = storeRepository.findAllWithKeyword(keyword, pageable);
		return storesPage.map(store -> map(store, isAdmin));
	}

	@Transactional(readOnly = true)
	@Override
	public Page<StoreResponseDto> getAllByCategory(UUID storeCategoryId, String keyword, Pageable pageable,
		UserAuth user) {
		authService.ensureAdmin(user);
		boolean isAdmin = true;
		Page<Store> storesPage = storeRepository.findAllByStoreCategoryWithKeyword(storeCategoryId, keyword, pageable);
		return storesPage.map(store -> map(store, isAdmin));

	}

	@Override
	public StoreResponseDto forceUpdateStatus(UUID storeId, StoreUpdateStatusRequestDto req, UserAuth user) {
		authService.ensureAdmin(user);
		boolean isAdmin = true;
		Store store = getStoreOrThrow(storeId);
		store.setStatus(req.getStatus());
		return map(store, isAdmin);
	}

	@Override
	public StoreResponseDto manageUpdate(UUID storeId, @Valid StoreRequestDto req, UserAuth user) {
		authService.ensureAdmin(user);
		boolean isAdmin = true;
		Store store = getStoreOrThrow(storeId);
		StoreCategory storeCategory = getStoreCategoryOrThrow(req.getStoreCategoryId());
		store.setName(req.getName());
		store.setDescription(req.getDescription());
		store.setAddress(req.getAddress());
		store.setStoreCategory(storeCategory);
		return map(store, isAdmin);
	}

	@Override
	public void delete(UUID storeId, UserAuth user) {
		authService.ensureAdmin(user);
		Store store = getStoreOrThrow(storeId);
		store.softDelete(user.getId());
		store.setStatus(StoreStatus.CLOSED);
	}

	@Override
	public StoreResponseDto getById(UUID storeId, UserAuth user) {
		authService.ensureAdmin(user);
		boolean isAdmin = true;
		return map(getStoreOrThrow(storeId), isAdmin);
	}

	@Override
	public StoreResponseDto restore(UUID storeId, UserAuth user) {
		authService.ensureAdmin(user);
		boolean isAdmin = true;
		Store store = getDeletedStoreOrThrow(storeId);
		store.restore(user.getId());
		return map(store, isAdmin);
	}

	/* ==================== 유틸 메서드 ==================== */

	public Store getStoreOrThrow(UUID storeId) {
		return storeRepository.findById(storeId)
			.orElseThrow(StoreNotFoundException::new);
	}

	private StoreCategory getStoreCategoryOrThrow(UUID storeCategoryId) {
		return storeCategoryRepository.findById(storeCategoryId)
			.orElseThrow(StoreCategoryNotFoundException::new);
	}

	private Store getDeletedStoreOrThrow(UUID storeId) {
		return storeRepository.findByIdAndDeletedAtIsNotNull(storeId)
			.orElseThrow(DeletedStoreNotFoundException::new);
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
