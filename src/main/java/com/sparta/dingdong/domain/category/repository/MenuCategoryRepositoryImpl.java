package com.sparta.dingdong.domain.category.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.dingdong.domain.category.entity.MenuCategory;
import com.sparta.dingdong.domain.category.entity.QMenuCategory;
import com.sparta.dingdong.domain.store.entity.QStore;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MenuCategoryRepositoryImpl implements MenuCategoryRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<MenuCategory> findByIdWithStore(UUID id) {
		QMenuCategory mc = QMenuCategory.menuCategory;
		QStore store = QStore.store;

		MenuCategory result = queryFactory
			.selectFrom(mc)
			.join(mc.store, store).fetchJoin()
			.where(mc.id.eq(id))
			.fetchOne();

		return Optional.ofNullable(result);
	}

	@Override
	public List<MenuCategory> findByStoreIdWithStore(UUID storeId) {
		QMenuCategory mc = QMenuCategory.menuCategory;
		QStore store = QStore.store;

		return queryFactory
			.selectFrom(mc)
			.join(mc.store, store).fetchJoin()
			.where(store.id.eq(storeId))
			.orderBy(mc.sortMenuCategory.asc())
			.fetch();
	}
}