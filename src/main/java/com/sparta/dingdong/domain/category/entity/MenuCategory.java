package com.sparta.dingdong.domain.category.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sparta.dingdong.common.base.BaseEntity;
import com.sparta.dingdong.domain.store.entity.Store;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
	name = "p_menu_category",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"store_id", "sort_menu_category"})
	}
) // store_id가 같다면 sort_menu_category 값이 중복되면 안됨
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuCategory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	// unique 제거 -> 메뉴카테고리는 이름 같을 수도 있음
	@Column(nullable = false)
	private String name;

	@Column(name = "sort_menu_category")
	private Integer sortMenuCategory;

	// 카테고리 삭제 시 연관된 MenuCategoryItem만 삭제되고, MenuItem은 삭제 안 됨
	@OneToMany(mappedBy = "menuCategory", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<MenuCategoryItem> items = new ArrayList<>();
}
