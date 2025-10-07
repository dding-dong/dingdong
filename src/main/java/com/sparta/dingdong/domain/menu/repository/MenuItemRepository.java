package com.sparta.dingdong.domain.menu.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sparta.dingdong.domain.menu.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {

	@Query("SELECT m FROM MenuItem m WHERE m.store.id = :storeId AND m.deletedAt IS NULL ORDER BY m.createdAt DESC")
	List<MenuItem> findActiveByStoreId(UUID storeId);

	@Query("SELECT m FROM MenuItem m WHERE m.store.id = :storeId")
	List<MenuItem> findAllByStoreIdIncludingDeleted(UUID storeId);
}
