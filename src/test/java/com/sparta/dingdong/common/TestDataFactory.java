package com.sparta.dingdong.common;

import java.math.BigInteger;
import java.util.UUID;

import com.sparta.dingdong.common.entity.Dong;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.cart.entity.Cart;
import com.sparta.dingdong.domain.category.entity.StoreCategory;
import com.sparta.dingdong.domain.menu.entity.MenuItem;
import com.sparta.dingdong.domain.store.entity.Store;
import com.sparta.dingdong.domain.store.entity.StoreDeliveryArea;
import com.sparta.dingdong.domain.store.entity.enums.StoreStatus;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.entity.enums.UserRole;

public class TestDataFactory {

	public static UserAuth createUserAuth(User user) {
		return new UserAuth(user.getId(), user.getUserRole(), 1L);
	}

	// ===== Users =====
	public static User createCustomer() {
		return User.builder()
			.id(1L)
			.email("customer2@example.com")
			.password("Customer123!")
			.username("customerUser")
			.nickname("김고객2")
			.phone("010-1111-1111")
			.userRole(UserRole.CUSTOMER)
			.build();
	}

	public static User createOwner() {
		return User.builder()
			.id(2L)
			.email("owner@example.com")
			.password("Owner123!")
			.username("ownerUser")
			.nickname("박주인")
			.phone("010-2222-2222")
			.userRole(UserRole.OWNER)
			.build();
	}

	// ===== StoreCategory =====
	public static StoreCategory createStoreCategory() {
		return StoreCategory.builder()
			.id(UUID.fromString("20227a32-fa45-4d87-a972-27f32b475024"))
			.name("한식")
			.description("한식 전문점")
			.imageUrl("https://image.url/korean.jpg")
			.build();
	}

	// ===== Store =====
	public static Store createStore(StoreCategory category, User owner) {
		return Store.builder()
			.id(UUID.fromString("246fdbcd-3258-4fd7-9353-38a4ac5ee2ff"))
			.owner(owner)
			.storeCategory(category)
			.name("맛있는 치킨집")
			.imageUrl("https://example.com/chicken.jpg")
			.address("서울시 강남구 테헤란로 123")
			.postalCode("06234")
			.minOrderPrice(BigInteger.valueOf(20000))
			.status(StoreStatus.OPEN)
			.build();
	}

	public static Store createStore2(StoreCategory category, User owner) {
		return Store.builder()
			.id(UUID.fromString("5e1a8f73-7c89-4e4c-b2a5-1bfa6b3b7c91"))
			.owner(owner)
			.storeCategory(category)
			.name("중식본점")
			.imageUrl("https://example.com/sushi.jpg")
			.address("서울시 강남구 테헤란로 124")
			.postalCode("06235")
			.minOrderPrice(BigInteger.valueOf(15000))
			.status(StoreStatus.OPEN)
			.build();
	}

	// ===== StoreDeliveryArea =====
	public static StoreDeliveryArea createDeliveryArea(Store store, Dong dong) {
		return StoreDeliveryArea.builder()
			.id(UUID.fromString("b6fce01c-25c3-4f9e-9f64-874b5d29b1e2"))
			.store(store)
			.dong(dong)
			.build();
	}

	// ===== MenuItems =====
	public static MenuItem createKimchiStew(Store store) {
		return MenuItem.builder()
			.id(UUID.fromString("96e49a63-3f80-4360-83c6-721c1ffc4a06"))
			.aiContent("국물이 진하고 깊은 김치찌개")
			.isActive(true)
			.isAiUsed(false)
			.isDisplayed(true)
			.isRecommended(false)
			.isSoldout(false)
			.name("김치찌개")
			.price(BigInteger.valueOf(8000))
			.store(store)
			.build();
	}

	public static MenuItem createDoenjangStew(Store store) {
		return MenuItem.builder()
			.id(UUID.fromString("f88eef64-a84a-450b-a797-3c4d255e68bb"))
			.aiContent("구수한 된장찌개")
			.isActive(true)
			.isAiUsed(false)
			.isDisplayed(true)
			.isRecommended(false)
			.isSoldout(false)
			.name("된장찌개")
			.price(BigInteger.valueOf(7000))
			.store(store)
			.build();
	}

	public static MenuItem createJjajang(Store store) {
		return MenuItem.builder()
			.id(UUID.fromString("697e6efb-19b0-4566-a2cd-de11ffc23d33"))
			.aiContent("달콤하고 진한 짜장면")
			.isActive(true)
			.isAiUsed(false)
			.isDisplayed(true)
			.isRecommended(true)
			.isSoldout(false)
			.name("짜장면")
			.price(BigInteger.valueOf(6000))
			.store(store)
			.build();
	}

	public static MenuItem createJjamppong(Store store) {
		return MenuItem.builder()
			.id(UUID.fromString("e705721d-ec2f-4096-8e5b-cb12a64dd70c"))
			.aiContent("얼큰하고 시원한 짬뽕")
			.isActive(true)
			.isAiUsed(false)
			.isDisplayed(true)
			.isRecommended(true)
			.isSoldout(false)
			.name("짬뽕")
			.price(BigInteger.valueOf(8500))
			.store(store)
			.build();
	}

	// ===== Cart 생성 예시 =====
	public static Cart createCart(User customer, Store store, MenuItem... items) {
		Cart cart = Cart.of(customer, store);
		for (MenuItem item : items) {
			cart.addItem(item, 1); // quantity 1로 추가
		}
		return cart;
	}
}
