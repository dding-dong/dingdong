package com.sparta.dingdong.domain.cart.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sparta.dingdong.common.base.BaseEntity;
import com.sparta.dingdong.domain.store.entity.Store;
import com.sparta.dingdong.domain.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
	name = "p_cart",
	uniqueConstraints = {
		@UniqueConstraint(name = "uq_cart_user", columnNames = {"user_id"})
	}
)
public class Cart extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartItem> items = new ArrayList<>();

	private Cart(User user, Store store) {
		this.user = user;
		this.store = store;
	}
}
