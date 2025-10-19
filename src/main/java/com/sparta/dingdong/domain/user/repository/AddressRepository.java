package com.sparta.dingdong.domain.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.user.entity.Address;
import com.sparta.dingdong.domain.user.exception.AddressNotFoundException;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

	Optional<Address> findById(Long addressId);

	default Address findByIdOrElseThrow(Long addressId) {
		return findById(addressId).orElseThrow(AddressNotFoundException::new);
	}

	// 특정 동에 거주하는 주소 조회
	List<Address> findByDongIdIn(List<String> dongIds);

	// 특정 구에 거주하는 주소 조회
	List<Address> findByDongGuIdIn(List<String> guId);

	// 특정 시/도에 거주하는 주소 조회
	List<Address> findByDongGuCityIdIn(List<String> cityId);
}
