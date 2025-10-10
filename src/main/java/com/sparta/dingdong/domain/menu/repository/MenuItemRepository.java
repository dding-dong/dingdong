package com.sparta.dingdong.domain.menu.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.dingdong.domain.menu.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, UUID>, MenuItemRepositoryCustom {
}
