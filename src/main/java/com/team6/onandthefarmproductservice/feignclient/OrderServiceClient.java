package com.team6.onandthefarmproductservice.feignclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.team6.onandthefarmproductservice.vo.order.OrderClientOrderProductIdResponse;
import com.team6.onandthefarmproductservice.vo.review.ReviewableProductResponse;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

	@GetMapping("/api/user/order-service/review-available/{user-no}")
	List<ReviewableProductResponse> findProductWithoutReview(@PathVariable("user-no") Long userId);

	@GetMapping("/api/user/order-service/order-product/{orders-no}")
	List<OrderClientOrderProductIdResponse> findByOrdersId(@PathVariable("orders-no") Long ordersId);

	@GetMapping("/api/user/order-service/order-product/{order-product-no}")
	OrderClientOrderProductIdResponse findProductIdByOrderProductId(@PathVariable("order-product-no") Long orderProductId);
}
