package com.team6.onandthefarmproductservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.team6.onandthefarmproductservice.vo.product.SellerClientSellerDetailResponse;

@FeignClient(name = "member-service")
public interface SellerServiceClient {
	@GetMapping("/api/seller/member-service/seller/{seller-no}")
	SellerClientSellerDetailResponse findBySellerId(@PathVariable("seller-no") Long sellerId);
}
