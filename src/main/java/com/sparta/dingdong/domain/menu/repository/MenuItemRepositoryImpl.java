package com.sparta.dingdong.domain.menu.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.dingdong.domain.menu.entity.MenuItem;
import com.sparta.dingdong.domain.menu.entity.QMenuItem;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MenuItemRepositoryImpl implements MenuItemRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QMenuItem menuItem = QMenuItem.menuItem;

	@Override
	public Page<MenuItem> findByStoreIdWithKeyword(UUID storeId, String keyword, Pageable pageable,
		boolean includeDeleted) {
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(menuItem.store.id.eq(storeId));
		if (!includeDeleted) {
			builder.and(menuItem.deletedAt.isNull());
		}
		if (keyword != null && !keyword.isBlank()) {
			builder.and(menuItem.name.lower().contains(keyword.toLowerCase()));
		}

		List<MenuItem> content = queryFactory
			.selectFrom(menuItem)
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(menuItem.createdAt.desc()) // 기본 정렬, 필요시 pageable.getSort() 적용 가능
			.fetch();

		Long total = queryFactory
			.select(menuItem.count())
			.from(menuItem)
			.where(builder)
			.fetchOne();
		total = total != null ? total : 0L;

		return new PageImpl<>(content, pageable, total);
	}
}
