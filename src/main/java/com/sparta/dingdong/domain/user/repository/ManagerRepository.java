package com.sparta.dingdong.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.user.entity.Manager;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {
}
