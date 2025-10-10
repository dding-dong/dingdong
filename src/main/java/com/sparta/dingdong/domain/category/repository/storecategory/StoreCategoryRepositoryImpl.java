package com.sparta.dingdong.domain.category.repository.storecategory;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.dingdong.domain.category.entity.QStoreCategory;
import com.sparta.dingdong.domain.category.entity.StoreCategory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StoreCategoryRepositoryImpl implements StoreCategoryRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QStoreCategory storeCategory = QStoreCategory.storeCategory;

	@Override
	public Page<StoreCategory> findAllByDeletedAtIsNullWithKeyword(String keyword, Pageable pageable) {
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(storeCategory.deletedAt.isNull());

		if (keyword != null && !keyword.isBlank()) {
			builder.and(storeCategory.name.lower().contains(keyword.toLowerCase()));
		}

		// 페이징 처리
		List<StoreCategory> content = queryFactory
			.selectFrom(storeCategory)
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(storeCategory.name.asc()) // 필요시 pageable.getSort() 적용 가능
			.fetch();

		Long total = queryFactory
			.select(storeCategory.count())
			.from(storeCategory)
			.where(builder)
			.fetchOne();
		total = total != null ? total : 0L;

		return new PageImpl<>(content, pageable, total);
	}
}
