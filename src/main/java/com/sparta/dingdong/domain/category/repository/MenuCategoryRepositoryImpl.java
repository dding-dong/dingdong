package com.sparta.dingdong.domain.category.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.dingdong.domain.category.entity.MenuCategory;
import com.sparta.dingdong.domain.category.entity.QMenuCategory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MenuCategoryRepositoryImpl implements MenuCategoryRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QMenuCategory menuCategory = QMenuCategory.menuCategory;

	@Override
	public Page<MenuCategory> findByStoreIdWithKeyword(UUID storeId, String keyword, Pageable pageable) {
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(menuCategory.store.id.eq(storeId))
			.and(menuCategory.deletedAt.isNull());

		if (keyword != null && !keyword.isBlank()) {
			builder.and(menuCategory.name.lower().contains(keyword.toLowerCase()));
		}

		List<MenuCategory> content = queryFactory
			.selectFrom(menuCategory)
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(menuCategory.sortMenuCategory.asc())
			.fetch();

		Long total = queryFactory
			.select(menuCategory.count())
			.from(menuCategory)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(content, pageable, total != null ? total : 0);
	}
}
