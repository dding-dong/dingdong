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
	public List<Store> findAll() {
		return queryFactory.selectFrom(store)
			.orderBy(store.name.asc())
			.fetch();
	}

	@Override
	public List<Store> findAllActive() {
		return queryFactory.selectFrom(store)
			.where(store.deletedAt.isNull())
			.orderBy(store.name.asc())
			.fetch();
	}

	@Override
	public List<Store> findAllActiveByCategory(UUID categoryId) {
		return queryFactory.selectFrom(store)
			.where(store.deletedAt.isNull()
				.and(store.category.id.eq(categoryId)))
			.orderBy(store.name.asc())
			.fetch();
	}

	@Override
	public List<Store> findAllByCategory(UUID categoryId) {
		return queryFactory.selectFrom(store)
			.where(store.category.id.eq(categoryId))
			.orderBy(store.name.asc())
			.fetch();
	}
}
