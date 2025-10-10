package com.sparta.dingdong.domain.category.repository.menucategory;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.category.entity.MenuCategoryItem;

@Repository
public interface MenuCategoryItemRepository
	extends JpaRepository<MenuCategoryItem, UUID>, MenuCategoryItemRepositoryCustom {

	boolean existsByMenuCategoryIdAndOrderNo(UUID categoryId, Integer orderNo);
}
