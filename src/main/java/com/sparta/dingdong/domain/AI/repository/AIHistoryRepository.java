package com.sparta.dingdong.domain.AI.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.AI.entity.AIHistory;

@Repository
public interface AIHistoryRepository extends JpaRepository<AIHistory, UUID> {
}
