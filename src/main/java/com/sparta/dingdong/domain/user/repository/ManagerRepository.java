package com.sparta.dingdong.domain.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.dingdong.domain.user.entity.Manager;
import com.sparta.dingdong.domain.user.entity.enums.ManagerStatus;

public interface ManagerRepository extends JpaRepository<Manager, Long> {

	List<Manager> findByStatus(ManagerStatus status);

	Optional<Manager> findByUserId(UUID userId);
}
