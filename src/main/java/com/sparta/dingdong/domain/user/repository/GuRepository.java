package com.sparta.dingdong.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.common.entity.Gu;

@Repository
public interface GuRepository extends JpaRepository<Gu, Long> {
}
