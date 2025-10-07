package com.sparta.dingdong.domain.store.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.dingdong.domain.store.entity.QStore;
import com.sparta.dingdong.domain.store.entity.Store;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QStore store = QStore.store;

	@Override
	public List<Store> findAllActive() {
		return queryFactory.selectFrom(store)
			.where(store.deletedAt.isNull())
			.orderBy(store.name.asc())
			.fetch();
	}

	@Override
	public List<Store> findAllActiveByStoreCategory(UUID storeCategoryId) {
		return queryFactory.selectFrom(store)
			.where(store.deletedAt.isNull()
				.and(store.storeCategory.id.eq(storeCategoryId)))
			.orderBy(store.name.asc())
			.fetch();
	}

	@Override
	public List<Store> findAllByStoreCategory(UUID storeCategoryId) {
		return queryFactory.selectFrom(store)
			.where(store.storeCategory.id.eq(storeCategoryId))
			.orderBy(store.name.asc())
			.fetch();
	}
}
