package com.sparta.dingdong.domain.store.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.store.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID>, StoreRepositoryCustom {

	Optional<Store> findByIdAndDeletedAtIsNotNull(UUID id);

}
