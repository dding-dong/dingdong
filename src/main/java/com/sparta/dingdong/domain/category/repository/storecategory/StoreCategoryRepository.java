package com.sparta.dingdong.domain.category.repository.storecategory;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.category.entity.StoreCategory;

@Repository
public interface StoreCategoryRepository extends JpaRepository<StoreCategory, UUID>, StoreCategoryRepositoryCustom {
}
