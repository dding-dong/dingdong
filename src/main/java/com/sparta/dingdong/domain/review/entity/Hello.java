package com.sparta.dingdong.domain.review.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Hello {

	@Id
	private Long id;

	private String name;

}
