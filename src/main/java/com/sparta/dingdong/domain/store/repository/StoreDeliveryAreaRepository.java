package com.sparta.dingdong.domain.store.repository;

import com.sparta.dingdong.domain.store.entity.StoreDeliveryArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StoreDeliveryAreaRepository extends JpaRepository<StoreDeliveryArea, UUID> {

    // 매장의 배달가능 지역 dong_id만 조회
    @Query("SELECT sda.dong.id FROM StoreDeliveryArea sda WHERE sda.store.id = :storeId")
    List<String> findDongIdsByStoreId(@Param("storeId") UUID storeId);
}
