package com.sparta.dingdong.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.user.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

}
