package com.sparta.dingdong.domain.store.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.store.entity.StoreDeliveryArea;

@Repository
public interface StoreDeliveryAreaRepository extends JpaRepository<StoreDeliveryArea, UUID> {
}
