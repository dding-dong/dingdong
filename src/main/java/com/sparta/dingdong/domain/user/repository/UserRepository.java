package com.sparta.dingdong.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.exception.UserErrorCode;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	default User findByEmailOrElseThrow(String email) {
		return findByEmail(email).orElseThrow(() -> new RuntimeException(UserErrorCode.NOT_FOUND_USER.getMessage()));
	}

	default User findByIdOrElseThrow(Long id) {
		return findById(id)
			.filter(user -> !user.isDeleted())
			.orElseThrow(() -> new RuntimeException(UserErrorCode.NOT_FOUND_USER.getMessage()));
	}
}
