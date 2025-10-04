package com.sparta.dingdong.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.common.entity.Dong;

import jakarta.validation.constraints.NotBlank;

@Repository
public interface DongRepository extends JpaRepository<Dong, Long> {
	Optional<Dong> findById(@NotBlank String dongId);
}
