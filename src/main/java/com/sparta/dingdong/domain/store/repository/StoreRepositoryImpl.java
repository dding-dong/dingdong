package com.sparta.dingdong.domain.store.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.dingdong.common.util.QuerydslUtils;
import com.sparta.dingdong.domain.category.entity.QStoreCategory;
import com.sparta.dingdong.domain.store.entity.QStore;
import com.sparta.dingdong.domain.store.entity.QStoreDeliveryArea;
import com.sparta.dingdong.domain.store.entity.Store;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QStore store = QStore.store;

	@Override
	public Page<Store> findAllActiveWithKeyword(String keyword, Pageable pageable, List<String> userDongIds) {
		return findAllWithKeyword(keyword, pageable, true, userDongIds);
	}

	@Override
	public Page<Store> findAllWithKeyword(String keyword, Pageable pageable) {
		return findAllWithKeyword(keyword, pageable, false, null);
	}

	@Override
	public Page<Store> findAllWithKeyword(String keyword, Pageable pageable, boolean onlyActive,
		List<String> userDongIds) {
		QStore store = QStore.store;
		QStoreCategory storeCategory = QStoreCategory.storeCategory;
		QStoreDeliveryArea deliveryArea = QStoreDeliveryArea.storeDeliveryArea;

		BooleanBuilder builder = new BooleanBuilder();
		if (onlyActive) {
			builder.and(store.deletedAt.isNull());
		}

		if (keyword != null && !keyword.isBlank()) {
			String lowered = keyword.toLowerCase();
			builder.andAnyOf(
				store.name.lower().contains(lowered),
				store.storeCategory.name.lower().contains(lowered)
			);
		}

		if (userDongIds != null && !userDongIds.isEmpty()) {
			builder.and(deliveryArea.dong.id.in(userDongIds));
		}

		List<Store> content = queryFactory
			.selectDistinct(store)
			.from(store)
			.leftJoin(store.storeCategory, storeCategory).fetchJoin()
			.leftJoin(store.deliveryAreas, deliveryArea).fetchJoin()
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(QuerydslUtils.toOrderSpecifiers(pageable.getSort(), store))
			.fetch();

		Long total = queryFactory
			.select(store.countDistinct())
			.from(store)
			.leftJoin(store.storeCategory, storeCategory)
			.leftJoin(store.deliveryAreas, deliveryArea)
			.where(builder)
			.fetchOne();

		total = total != null ? total : 0L;

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public Page<Store> findAllActiveByStoreCategoryWithKeyword(UUID storeCategoryId, String keyword,
		Pageable pageable, List<String> userDongIds) {
		QStore store = QStore.store;
		QStoreDeliveryArea deliveryArea = QStoreDeliveryArea.storeDeliveryArea;

		// 조건 빌더
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(store.deletedAt.isNull())
			.and(store.storeCategory.id.eq(storeCategoryId));

		if (keyword != null && !keyword.isBlank()) {
			builder.and(store.name.lower().contains(keyword.toLowerCase()));
		}

		if (userDongIds != null && !userDongIds.isEmpty()) {
			builder.and(deliveryArea.dong.id.in(userDongIds));
		}

		// 데이터 조회 (페이징)
		List<Store> content = queryFactory
			.selectFrom(store)
			.leftJoin(store.deliveryAreas, deliveryArea)
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(QuerydslUtils.toOrderSpecifiers(pageable.getSort(), store)) // 정렬 처리
			.fetch();

		// 전체 개수 조회
		Long total = queryFactory
			.select(store.countDistinct())
			.from(store)
			.leftJoin(store.deliveryAreas, deliveryArea)
			.where(builder)
			.fetchOne();
		total = total != null ? total : 0L;

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public Page<Store> findByOwnerIdWithKeyword(Long ownerId, String keyword, Pageable pageable) {
		QStore store = QStore.store;

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(store.deletedAt.isNull())
			.and(store.owner.id.eq(ownerId));

		if (keyword != null && !keyword.isBlank()) {
			builder.and(store.name.lower().contains(keyword.toLowerCase()));
		}

		List<Store> content = queryFactory
			.selectFrom(store)
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(QuerydslUtils.toOrderSpecifiers(pageable.getSort(), store))
			.fetch();

		Long total = queryFactory
			.select(store.count())
			.from(store)
			.where(builder)
			.fetchOne();
		total = total != null ? total : 0L;

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public Page<Store> findAllByStoreCategoryWithKeyword(UUID storeCategoryId, String keyword, Pageable pageable) {
		QStore store = QStore.store;

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(store.deletedAt.isNull())
			.and(store.storeCategory.id.eq(storeCategoryId));

		if (keyword != null && !keyword.isBlank()) {
			builder.and(store.name.lower().contains(keyword.toLowerCase()));
		}

		List<Store> content = queryFactory
			.selectFrom(store)
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(QuerydslUtils.toOrderSpecifiers(pageable.getSort(), store))
			.fetch();

		Long total = queryFactory
			.select(store.count())
			.from(store)
			.where(builder)
			.fetchOne();
		total = total != null ? total : 0L;

		return new PageImpl<>(content, pageable, total);
	}
}
