package com.sparta.dingdong.domain.store.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.store.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID>, StoreRepositoryCustom {
	// OWNER가 본인 가게만 조회할 때 사용
	List<Store> findByOwnerId(Long ownerId);

	@Query("select st from Store st where st.id = :id and st.deletedAt is not null")
	Optional<Store> findDeletedById(@Param("id") UUID id);
}
