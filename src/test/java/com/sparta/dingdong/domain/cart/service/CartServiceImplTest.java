package com.sparta.dingdong.domain.cart.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigInteger;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.sparta.dingdong.common.TestDataFactory;
import com.sparta.dingdong.common.TestJasyptConfig;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.cart.dto.request.AddCartItemRequestDto;
import com.sparta.dingdong.domain.cart.dto.response.CartItemResponseDto;
import com.sparta.dingdong.domain.cart.dto.response.CartResponseDto;
import com.sparta.dingdong.domain.cart.entity.Cart;
import com.sparta.dingdong.domain.cart.exception.CartNotFoundException;
import com.sparta.dingdong.domain.cart.exception.CartStoreConflictException;
import com.sparta.dingdong.domain.cart.exception.InvalidCartQuantityException;
import com.sparta.dingdong.domain.cart.repository.CartRepository;
import com.sparta.dingdong.domain.category.entity.StoreCategory;
import com.sparta.dingdong.domain.menu.entity.MenuItem;
import com.sparta.dingdong.domain.menu.service.MenuItemService;
import com.sparta.dingdong.domain.store.entity.Store;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.service.UserService;

@SpringBootTest(classes = {CartServiceImpl.class, TestJasyptConfig.class})
class CartServiceImplTest {

	@Autowired
	CartService cartService;

	@MockBean
	CartRepository cartRepository;

	@MockBean
	MenuItemService menuItemService;
	@MockBean
	UserService userService;

	private User customer;
	private UserAuth userAuth;
	private User owner;
	private StoreCategory category;
	private Store store;
	private Store store2;
	private MenuItem kimchi;
	private MenuItem doenjang;
	private MenuItem jjajang;
	private MenuItem jjamppong;

	@BeforeEach
	void setUp() {
		customer = TestDataFactory.createCustomer();
		owner = TestDataFactory.createOwner();
		category = TestDataFactory.createStoreCategory();
		store = TestDataFactory.createStore(category, owner);
		store2 = TestDataFactory.createStore2(category, owner);

		kimchi = TestDataFactory.createKimchiStew(store);
		doenjang = TestDataFactory.createDoenjangStew(store);

		jjajang = TestDataFactory.createJjajang(store2);
		jjamppong = TestDataFactory.createJjamppong(store2);

		userAuth = TestDataFactory.createUserAuth(customer);
	}

	@DisplayName("장바구니 있으면 조회")
	@Test
	void getCart_success() {
		//given
		when(userService.findByUser(any(UserAuth.class))).thenReturn(customer);
		when(menuItemService.findById(kimchi.getId())).thenReturn(kimchi);

		AddCartItemRequestDto request = AddCartItemRequestDto.builder()
			.menuItemId(kimchi.getId())
			.quantity(2)
			.build();

		when(cartRepository.save(any(Cart.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		//when
		CartResponseDto response = cartService.addItem(userAuth, request, false);

		//then
		assertNotNull(response);
		assertEquals(store.getId(), response.getStoreId());
		assertEquals(store.getName(), response.getStoreName());
		assertEquals(1, response.getItems().size());

		CartItemResponseDto itemResponse = response.getItems().get(0);
		assertEquals(kimchi.getId(), itemResponse.getMenuItemId());
		assertEquals(2, itemResponse.getQuantity());

		BigInteger expectedTotal = kimchi.getPrice().multiply(BigInteger.valueOf(2));
		assertEquals(expectedTotal, response.getTotalPrice());

		verify(cartRepository, times(1)).findByUserId(customer.getId());
		verify(userService, times(1)).findByUser(any(UserAuth.class));
	}

	@DisplayName("장바구니 없으면 예외처리")
	@Test
	void getCart_exception() {
		// given
		when(userService.findByUser(any(UserAuth.class))).thenReturn(customer);
		when(cartRepository.findByUserId(customer.getId())).thenReturn(Optional.empty());

		// when & then
		assertThrows(CartNotFoundException.class, () -> cartService.getCart(userAuth));

		verify(cartRepository, times(1)).findByUserId(customer.getId());
	}

	@DisplayName("장바구니에 다른 가게가 있으면 replace=false일 때 예외처리")
	@Test
	void addItem_replace_false() {
		// given
		when(userService.findByUser(any(UserAuth.class))).thenReturn(customer);
		when(menuItemService.findById(kimchi.getId())).thenReturn(kimchi);

		Cart existingCart = TestDataFactory.createCart(customer, store2, jjajang);

		when(cartRepository.findByUserId(customer.getId())).thenReturn(Optional.of(existingCart));

		AddCartItemRequestDto request = AddCartItemRequestDto.builder()
			.menuItemId(kimchi.getId())
			.quantity(2)
			.build();
		when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

		// when & then
		assertThrows(CartStoreConflictException.class, () -> cartService.addItem(userAuth, request, false));

		// 기존 장바구니를 조회했는지 확인
		verify(cartRepository, times(1)).findByUserId(customer.getId());
		verify(menuItemService, times(1)).findById(kimchi.getId());

		verify(cartRepository, never()).save(any(Cart.class));
	}

	@DisplayName("장바구니에 다른 가게가 있으면 replace=true일 때 기존 장바구니 교체하고 추가")
	@Test
	void addItem_replace_true() {
		// given
		when(userService.findByUser(any(UserAuth.class))).thenReturn(customer);
		when(menuItemService.findById(kimchi.getId())).thenReturn(kimchi);

		// store2 메뉴 장바구니에 추가
		Cart existingCart = TestDataFactory.createCart(customer, store2, jjajang);

		when(cartRepository.findByUserId(customer.getId())).thenReturn(Optional.of(existingCart));

		when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

		// store1 메뉴 장바구니에 추가
		AddCartItemRequestDto request = AddCartItemRequestDto.builder()
			.menuItemId(kimchi.getId())
			.quantity(2)
			.build();

		// when
		CartResponseDto response = cartService.addItem(userAuth, request, true);

		// then
		assertNotNull(response);
		assertEquals(kimchi.getStore().getId(), response.getStoreId());
		assertEquals(kimchi.getStore().getName(), response.getStoreName());
		assertEquals(1, response.getItems().size());

		CartItemResponseDto itemResponse = response.getItems().get(0);
		assertEquals(kimchi.getId(), itemResponse.getMenuItemId());
		assertEquals(2, itemResponse.getQuantity());

		BigInteger expectedTotal = kimchi.getPrice().multiply(BigInteger.valueOf(2));
		assertEquals(expectedTotal, response.getTotalPrice());

		verify(cartRepository, times(1)).findByUserId(customer.getId());
		verify(userService, times(1)).findByUser(any(UserAuth.class));
	}

	@DisplayName("장바구니 메뉴 수량 변경 성공")
	@Test
	void updateItemQuantity_success() {
		//given
		when(userService.findByUser(any(UserAuth.class))).thenReturn(customer);
		when(menuItemService.findById(kimchi.getId())).thenReturn(kimchi);

		Cart existingCart = TestDataFactory.createCart(customer, store, kimchi);
		existingCart.addItem(kimchi, 2);

		when(cartRepository.findByUserId(customer.getId()))
			.thenReturn(Optional.of(existingCart));

		when(cartRepository.save(any(Cart.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		int newQuantity = 3;

		// when
		CartResponseDto response = cartService.updateItemQuantity(userAuth, kimchi.getId(), newQuantity);

		// then
		assertNotNull(response);
		assertEquals(store.getId(), response.getStoreId());
		assertEquals(1, response.getItems().size());

		CartItemResponseDto updatedItem = response.getItems().get(0);
		assertEquals(kimchi.getId(), updatedItem.getMenuItemId());
		assertEquals(newQuantity, updatedItem.getQuantity());

		BigInteger expectedTotal = kimchi.getPrice().multiply(BigInteger.valueOf(newQuantity));
		assertEquals(expectedTotal, response.getTotalPrice());

		verify(cartRepository, times(1)).findByUserId(customer.getId());
		verify(cartRepository, times(1)).save(any(Cart.class));
	}

	@DisplayName("장바구니 메뉴 수량 0으로 변경 예외처리")
	@Test
	void updateItemQuantity_exception() {
		//given
		when(userService.findByUser(any(UserAuth.class))).thenReturn(customer);
		when(menuItemService.findById(kimchi.getId())).thenReturn(kimchi);

		Cart existingCart = TestDataFactory.createCart(customer, store, kimchi);
		existingCart.addItem(kimchi, 2);

		when(cartRepository.findByUserId(customer.getId()))
			.thenReturn(Optional.of(existingCart));

		when(cartRepository.save(any(Cart.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		int newQuantity = 0;

		// when & then
		assertThrows(
			InvalidCartQuantityException.class,
			() -> cartService.updateItemQuantity(userAuth, kimchi.getId(), newQuantity)
		);

		verify(cartRepository, times(1)).findByUserId(customer.getId());
		verify(cartRepository, never()).save(any(Cart.class));

	}
}