package com.sparta.dingdong.domain.category.repository;

import com.sparta.dingdong.domain.category.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, UUID> {
    List<MenuCategory> findByStoreId(UUID storeId);
}
