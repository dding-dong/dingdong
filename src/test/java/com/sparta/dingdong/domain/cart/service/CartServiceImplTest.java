package com.sparta.dingdong.domain.cart.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sparta.dingdong.common.TestDataFactory;
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
import com.sparta.dingdong.domain.menu.exception.MenuItemNotFoundException;
import com.sparta.dingdong.domain.menu.exception.MenuItemSoldOutException;
import com.sparta.dingdong.domain.menu.service.MenuItemService;
import com.sparta.dingdong.domain.store.entity.Store;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.service.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService 테스트")
class CartServiceImplTest {

	@InjectMocks
	CartServiceImpl cartService;

	@Mock
	CartRepository cartRepository;

	@Mock
	MenuItemService menuItemService;
	@Mock
	UserService userService;

	private User customer;
	private UserAuth userAuth;
	private User owner;
	private StoreCategory category;
	private Store store;
	private Store store2;
	private MenuItem kimchi;
	private MenuItem doenjang; // soldOut 메뉴
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

		when(userService.findByUser(any(UserAuth.class))).thenReturn(customer);
	}

	@Nested
	@DisplayName("장바구니 조회")
	class GetCartTest {
		@DisplayName("장바구니에 메뉴 담아둔거 없으면 조회되지 않아 예외처리")
		@Test
		void getCart_exception() {
			// given
			when(cartRepository.findByUserId(customer.getId())).thenReturn(Optional.empty());

			// when & then
			assertThrows(CartNotFoundException.class, () -> cartService.getCart(userAuth));

			verify(cartRepository, times(1)).findByUserId(customer.getId());
		}
	}

	@Nested
	@DisplayName("장바구니 아이템 추가")
	class AddItemToCartTest {
		@DisplayName("장바구니가 없을 때 addItem을 호출하면 새 장바구니가 생성된다.")
		@Test
		void addItem_whenCartNotExists_shouldCreateNewCart() {
			final int QTY_2 = 2;

			//given
			when(menuItemService.findById(kimchi.getId())).thenReturn(kimchi);

			AddCartItemRequestDto request = AddCartItemRequestDto.builder()
				.menuItemId(kimchi.getId())
				.quantity(2)
				.build();

			when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

			//when
			CartResponseDto response = cartService.addItem(userAuth, request, false);

			//then
			assertNotNull(response);
			assertEquals(store.getId(), response.getStoreId());
			assertEquals(store.getName(), response.getStoreName());
			assertEquals(1, response.getItems().size());

			CartItemResponseDto itemResponse = response.getItems().get(0);
			assertEquals(kimchi.getId(), itemResponse.getMenuItemId());
			assertEquals(QTY_2, itemResponse.getQuantity());

			BigInteger expectedTotal = kimchi.getPrice().multiply(BigInteger.valueOf(QTY_2));
			assertEquals(expectedTotal, response.getTotalPrice());

			verify(cartRepository, times(1)).findByUserId(customer.getId());
			verify(userService, times(1)).findByUser(any(UserAuth.class));
			verify(menuItemService, times(1)).findById(kimchi.getId());
		}

		@DisplayName("품절된 메뉴 addItem 호출 시 예외처리")
		@Test
		void addItem_soldOut_menuItem() {
			// given
			when(menuItemService.findById(doenjang.getId())).thenReturn(doenjang);

			AddCartItemRequestDto request = AddCartItemRequestDto.builder()
				.menuItemId(doenjang.getId())
				.quantity(2)
				.build();

			// when & then
			assertThrows(MenuItemSoldOutException.class, () -> cartService.addItem(userAuth, request, false));
		}

		// 동일 메뉴 여러번 addItem 호출 시 수량 합산 확인
		@DisplayName("동일 메뉴 여러번 addItem 호출 시 수량 합산")
		@Test
		void addItem_existing_menuItem() {
			// given
			final int QTY_2 = 2;

			when(menuItemService.findById(kimchi.getId())).thenReturn(kimchi);

			Cart existingCart = TestDataFactory.createCart(customer, store, kimchi); // 1개 들어 있음

			when(cartRepository.findByUserId(eq(customer.getId()))).thenReturn(Optional.of(existingCart));
			when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

			AddCartItemRequestDto request = AddCartItemRequestDto.builder()
				.menuItemId(kimchi.getId())
				.quantity(QTY_2) // 2개 추가
				.build();

			// when
			CartResponseDto response = cartService.addItem(userAuth, request, false);

			// then
			assertNotNull(response);
			assertEquals(store.getId(), response.getStoreId());
			assertEquals(store.getName(), response.getStoreName());
			assertEquals(1, response.getItems().size());

			CartItemResponseDto itemResponse = response.getItems().get(0);
			assertEquals(kimchi.getId(), itemResponse.getMenuItemId());
			assertEquals(QTY_2 + 1, itemResponse.getQuantity());

			BigInteger expectedTotal = kimchi.getPrice().multiply(BigInteger.valueOf(QTY_2 + 1));
			assertEquals(expectedTotal, response.getTotalPrice());

			verify(userService, times(1)).findByUser(any(UserAuth.class));
			verify(menuItemService, times(1)).findById(kimchi.getId());
			verify(cartRepository, times(1)).findByUserId(customer.getId());
		}

		@DisplayName("존재하지 않은 menuItem addItem 호출 시 예외처리")
		@Test
		void addItem_nonExisting_menuItem() {
			// given
			final UUID nonExistingMenuItemId = UUID.randomUUID();

			when(menuItemService.findById(nonExistingMenuItemId)).thenThrow(new MenuItemNotFoundException());

			AddCartItemRequestDto request = AddCartItemRequestDto.builder()
				.menuItemId(nonExistingMenuItemId)
				.quantity(1)
				.build();

			// when & then
			assertThrows(MenuItemNotFoundException.class, () -> cartService.addItem(userAuth, request, false));

			verify(userService, times(1)).findByUser(any(UserAuth.class));
			verify(menuItemService, times(1)).findById(nonExistingMenuItemId);
			verify(cartRepository, never()).save(any(Cart.class));
		}

		@DisplayName("장바구니에 다른 가게가 있으면 replace=false일 때 예외처리")
		@Test
		void addItem_replace_false() {
			final int QTY_2 = 2;

			// given
			when(menuItemService.findById(kimchi.getId())).thenReturn(kimchi);

			Cart existingCart = TestDataFactory.createCart(customer, store2, jjajang);

			when(cartRepository.findByUserId(customer.getId())).thenReturn(Optional.of(existingCart));

			AddCartItemRequestDto request = AddCartItemRequestDto.builder()
				.menuItemId(kimchi.getId())
				.quantity(QTY_2)
				.build();

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
			final int QTY_2 = 2;

			// given
			when(menuItemService.findById(kimchi.getId())).thenReturn(kimchi);

			// store2 메뉴 장바구니에 추가
			Cart existingCart = TestDataFactory.createCart(customer, store2, jjajang);

			when(cartRepository.findByUserId(customer.getId())).thenReturn(Optional.of(existingCart));
			when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

			// store1 메뉴 장바구니에 추가
			AddCartItemRequestDto request = AddCartItemRequestDto.builder()
				.menuItemId(kimchi.getId())
				.quantity(QTY_2)
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
			assertEquals(QTY_2, itemResponse.getQuantity());

			BigInteger expectedTotal = kimchi.getPrice().multiply(BigInteger.valueOf(QTY_2));
			assertEquals(expectedTotal, response.getTotalPrice());

			verify(cartRepository, times(1)).findByUserId(customer.getId());
			verify(userService, times(1)).findByUser(any(UserAuth.class));
		}
	}

	@Nested
	@DisplayName("장바구니 아이템 수량 변경")
	class updateItemQuantity {
		@DisplayName("장바구니 메뉴 수량 변경 성공")
		@Test
		void updateItemQuantity_success() {
			final int QTY_3 = 3;

			//given
			Cart existingCart = TestDataFactory.createCart(customer, store, kimchi);
			existingCart.addItem(kimchi, 2);

			when(cartRepository.findByUserId(customer.getId())).thenReturn(Optional.of(existingCart));
			when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

			// when
			CartResponseDto response = cartService.updateItemQuantity(userAuth, kimchi.getId(), QTY_3);

			// then
			assertNotNull(response);
			assertEquals(store.getId(), response.getStoreId());
			assertEquals(1, response.getItems().size());

			CartItemResponseDto updatedItem = response.getItems().get(0);
			assertEquals(kimchi.getId(), updatedItem.getMenuItemId());
			assertEquals(QTY_3, updatedItem.getQuantity());

			BigInteger expectedTotal = kimchi.getPrice().multiply(BigInteger.valueOf(QTY_3));
			assertEquals(expectedTotal, response.getTotalPrice());

			verify(cartRepository, times(1)).findByUserId(customer.getId());
			verify(cartRepository, times(1)).save(any(Cart.class));
		}

		@DisplayName("장바구니 메뉴 수량 0으로 변경하면 예외처리")
		@Test
		void updateItemQuantity_exception() {
			final int QTY_0 = 0;

			//given
			Cart existingCart = TestDataFactory.createCart(customer, store, kimchi);
			existingCart.addItem(kimchi, 2);

			when(cartRepository.findByUserId(customer.getId())).thenReturn(Optional.of(existingCart));

			// when & then
			assertThrows(
				InvalidCartQuantityException.class,
				() -> cartService.updateItemQuantity(userAuth, kimchi.getId(), QTY_0)
			);

			verify(cartRepository, times(1)).findByUserId(customer.getId());
			verify(cartRepository, never()).save(any(Cart.class));

		}
	}

	// 장바구니에 담긴 메뉴 삭제 - 성공
	// 장바구니에 담긴 마지막 메뉴 삭제 시 장바구니도 함께 삭제
	// 장바구니에 담긴 메뉴가 아닌 경우 예외처리

	// 장바구니 삭제
	@Nested
	@DisplayName("장바구니 삭제")
	class deleteCart {
		@DisplayName("장바구니 삭제 성공")
		@Test
		void deleteCart_success() {
			// given
			Cart existingCart = TestDataFactory.createCart(customer, store, kimchi); // 1개 들어 있음

			when(cartRepository.findByUserId(eq(customer.getId()))).thenReturn(Optional.of(existingCart));

			doAnswer(invocation -> {
				when(cartRepository.findByUserId(customer.getId())).thenReturn(Optional.empty());
				return null;
			}).when(cartRepository).delete(any(Cart.class));

			// when
			cartService.deleteCart(userAuth);

			// then
			assertThrows(CartNotFoundException.class, () -> cartService.getCart(userAuth));
		}
	}
}