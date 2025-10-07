package com.sparta.dingdong.domain.category.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.category.entity.StoreCategory;

@Repository
public interface StoreCategoryRepository extends JpaRepository<StoreCategory, UUID> {

	// 소프트 딜리트되지 않은 카테고리만 조회
	List<StoreCategory> findAllByDeletedAtIsNull();

	// 선택적으로, 특정 ID 조회 시에도 soft delete 체크
	Optional<StoreCategory> findByIdAndDeletedAtIsNull(UUID id);
}
