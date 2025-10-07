package com.sparta.dingdong.domain.category.repository;

import java.util.Optional;
import java.util.UUID;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.dingdong.domain.category.entity.MenuCategoryItem;
import com.sparta.dingdong.domain.category.entity.QMenuCategory;
import com.sparta.dingdong.domain.category.entity.QMenuCategoryItem;
import com.sparta.dingdong.domain.store.entity.QStore;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MenuCategoryItemRepositoryImpl implements MenuCategoryItemRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<MenuCategoryItem> findByIdWithMenuCategoryAndStore(UUID id) {
		QMenuCategoryItem mci = QMenuCategoryItem.menuCategoryItem;
		QMenuCategory mc = QMenuCategory.menuCategory;
		QStore store = QStore.store;

		MenuCategoryItem result = queryFactory
			.selectFrom(mci)
			.join(mci.menuCategory, mc).fetchJoin()
			.join(mc.store, store).fetchJoin()
			.where(mci.id.eq(id))
			.fetchOne();

		return Optional.ofNullable(result);
	}
}
