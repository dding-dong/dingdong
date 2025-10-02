package com.sparta.dingdong.domain.category.repository;

import com.sparta.dingdong.domain.category.entity.MenuCategoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuCategoryItemRepository extends JpaRepository<MenuCategoryItem, UUID> {
    List<MenuCategoryItem> findByMenuCategoryId(UUID menuCategoryId);
}
