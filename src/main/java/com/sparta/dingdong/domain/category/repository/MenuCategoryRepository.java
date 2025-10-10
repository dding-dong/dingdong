package com.sparta.dingdong.domain.category.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.category.entity.MenuCategory;
import com.sparta.dingdong.domain.store.entity.Store;

@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, UUID>, MenuCategoryRepositoryCustom {

	// 특정 ID로 fetch join (store 포함)
	@Query("select mc from MenuCategory mc join fetch mc.store where mc.id = :id")
	Optional<MenuCategory> findByIdWithStore(@Param("id") UUID id);

	// 삭제된 카테고리 조회
	@Query("select mc from MenuCategory mc where mc.id = :id and mc.deletedAt is not null")
	Optional<MenuCategory> findDeletedById(@Param("id") UUID id);

	boolean existsByStoreAndSortMenuCategory(Store store, Integer sortMenuCategory);
}
