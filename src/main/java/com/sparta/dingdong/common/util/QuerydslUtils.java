package com.sparta.dingdong.common.util;

import org.springframework.data.domain.Sort;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class QuerydslUtils {
	
	public static OrderSpecifier<?>[] toOrderSpecifiers(Sort sort, EntityPathBase<?> entityPath) {
		return sort.stream()
			.map(order -> {
				PathBuilder<?> pathBuilder = new PathBuilder<>(entityPath.getType(), entityPath.getMetadata());

				return new OrderSpecifier<>(
					order.isAscending() ? Order.ASC : Order.DESC,
					pathBuilder.getComparable(order.getProperty(), Comparable.class)
				);
			})
			.toArray(OrderSpecifier[]::new);
	}
}
