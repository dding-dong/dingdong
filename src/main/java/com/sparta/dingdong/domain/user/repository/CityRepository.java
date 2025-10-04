package com.sparta.dingdong.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.common.entity.City;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
}
