package com.sparta.dingdong.domain.category.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.dingdong.domain.category.entity.MenuCategoryItem;
import com.sparta.dingdong.domain.category.entity.QMenuCategory;
import com.sparta.dingdong.domain.category.entity.QMenuCategoryItem;
import com.sparta.dingdong.domain.menu.entity.QMenuItem;
import com.sparta.dingdong.domain.store.entity.QStore;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MenuCategoryItemRepositoryImpl implements MenuCategoryItemRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QMenuCategoryItem menuCategoryItem = QMenuCategoryItem.menuCategoryItem;
	private final QMenuItem menuItem = QMenuItem.menuItem;

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

	@Override
	public Page<MenuCategoryItem> findByMenuCategoryIdWithKeyword(UUID menuCategoryId, String keyword,
		Pageable pageable) {

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(menuCategoryItem.menuCategory.id.eq(menuCategoryId))
			.and(menuCategoryItem.deletedAt.isNull())
			.and(menuItem.deletedAt.isNull());

		// 🔍 메뉴 이름 기준 검색
		if (keyword != null && !keyword.isBlank()) {
			builder.and(menuItem.name.lower().contains(keyword.toLowerCase()));
		}

		// 실제 데이터 조회 (페이징 + 정렬)
		List<MenuCategoryItem> content = queryFactory
			.selectFrom(menuCategoryItem)
			.join(menuCategoryItem.menuItem, menuItem).fetchJoin()
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(menuCategoryItem.orderNo.asc())
			.fetch();

		// 전체 개수 조회
		Long total = queryFactory
			.select(menuCategoryItem.count())
			.from(menuCategoryItem)
			.join(menuCategoryItem.menuItem, menuItem)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(content, pageable, total != null ? total : 0L);
	}
}
