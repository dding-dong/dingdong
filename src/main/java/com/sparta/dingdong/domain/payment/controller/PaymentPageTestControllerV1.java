package com.sparta.dingdong.domain.payment.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.service.OrderService;
import com.sparta.dingdong.domain.payment.dto.request.ConfirmPaymentPageRequestDto;
import com.sparta.dingdong.domain.payment.dto.response.ConfirmPaymentPageResponseDto;
import com.sparta.dingdong.domain.payment.service.PaymentPageService;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PaymentPageTestControllerV1 {

	private final OrderService orderService;
	private final UserService userService;
	private final PaymentPageService paymentPageService;

	@GetMapping("/toss")
	public String index(@RequestParam("userId") Long userId, Model model) {
		// 뷰에 전달
		model.addAttribute("userId", userId);

		return "index"; // src/main/resources/templates/index.html
	}

	@GetMapping("/orders")
	public String showOrders(@RequestParam("userId") Long userId, Model model) {

		List<Order> orders = orderService.findByOrders(userId); // userId로 주문 조회
		model.addAttribute("orders", orders);
		model.addAttribute("userId", userId); // 돌아가기 버튼용
		return "order-list";
	}

	// order-list.html에서 GET으로 넘어온 파라미터 받기
	@GetMapping("/checkout")
	public String checkout(@RequestParam("userId") Long userId, @RequestParam("orderId") UUID orderId,
		@RequestParam("orderName") String orderName, @RequestParam("amount") Long amount, Model model) {

		User user = userService.findByUser(userId);
		Order order = orderService.findByOrder(orderId);

		List<String> itemNames = order.getOrderItems()
			.stream()
			.map(oi -> oi.getMenuItem().getName())
			.collect(Collectors.toList());
		model.addAttribute("order", order);

		// view에 전달
		model.addAttribute("user", user);
		model.addAttribute("order", order);
		model.addAttribute("itemNames", itemNames);
		return "checkout"; // src/main/resources/templates/checkout.html
	}

	@GetMapping("/success")
	public String success(@RequestParam("userId") Long userId, Model model) {
		model.addAttribute("userId", userId);
		return "success";
	}

	@GetMapping("/fail")
	public String fail(@RequestParam("userId") Long userId, Model model) {
		model.addAttribute("userId", userId);
		return "fail";
	}

	@RequestMapping(value = "/test/payment/confirm", method = RequestMethod.POST)
	public ResponseEntity<BaseResponseDto<?>> confirmPayment(@RequestBody ConfirmPaymentPageRequestDto requestDto) {

		ConfirmPaymentPageResponseDto response = paymentPageService.confirmPayment(requestDto);

		return ResponseEntity.ok(BaseResponseDto.success("결제 요청을 승인합니다.", response));
	}
}
