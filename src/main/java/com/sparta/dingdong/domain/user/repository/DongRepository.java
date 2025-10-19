package com.sparta.dingdong.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.common.entity.Dong;
import com.sparta.dingdong.domain.user.exception.DongNotFoundException;

@Repository
public interface DongRepository extends JpaRepository<Dong, Long> {

	Optional<Dong> findById(String dongId);

	default Dong findByIdOrElseThrow(String dongId) {
		return findById(dongId).orElseThrow(DongNotFoundException::new);
	}

}
