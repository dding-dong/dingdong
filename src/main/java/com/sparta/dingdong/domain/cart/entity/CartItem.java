package com.sparta.dingdong.domain.cart.entity;

import java.math.BigInteger;
import java.util.UUID;

import com.sparta.dingdong.common.base.BaseEntity;
import com.sparta.dingdong.domain.menu.entity.MenuItem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
	name = "p_cart_item",
	uniqueConstraints = {
		@UniqueConstraint(name = "uq_cart_item_cart_menu", columnNames = {"cart_id", "menu_item_id"})
	}
)
public class CartItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cart_id", nullable = false, updatable = false)
	private Cart cart;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "menu_item_id", nullable = false)
	private MenuItem menuItem;

	@Column(nullable = false)
	private Integer quantity = 1;

	@Column(nullable = false)
	private BigInteger unitPrice;

	private CartItem(Cart cart, MenuItem menuItem, Integer quantity, BigInteger unitPrice) {
		this.cart = cart;
		this.menuItem = menuItem;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
	}
}
