package com.team6.onandthefarmproductservice.feignclient;

import java.util.List;

import com.team6.onandthefarmproductservice.vo.order.OrderClientSellerIdAndDateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.team6.onandthefarmproductservice.vo.order.OrderClientOrderProductIdResponse;
import com.team6.onandthefarmproductservice.vo.order.OrdersByUserResponse;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

	@GetMapping("/api/user/orders/order-service/review-available/{user-no}")
	List<OrdersByUserResponse> findProductWithoutReview(@PathVariable("user-no") Long userId);

	@GetMapping("/api/user/orders/order-service/order-product/{orders-no}")
	List<OrderClientOrderProductIdResponse> findByOrdersId(@PathVariable("orders-no") Long ordersId);

	@GetMapping("/api/user/orders/order-service/{order-product-no}")
	OrderClientOrderProductIdResponse findProductIdByOrderProductId(@PathVariable("order-product-no") Long orderProductId);

	@GetMapping("/api/user/orders/order-service/order-product/orders-list")
	List<OrderClientSellerIdAndDateResponse> findBySellerIdAndOrderProductDateStartingWith(@RequestParam Long sellerId, @RequestParam String nextDate);

}
