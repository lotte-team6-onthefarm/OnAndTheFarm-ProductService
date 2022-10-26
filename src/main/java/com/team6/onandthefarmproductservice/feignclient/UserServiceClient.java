package com.team6.onandthefarmproductservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.team6.onandthefarmproductservice.vo.product.UserClientUserShortInfoResponse;

@FeignClient(name = "member-service", contextId = "member-service-user")
public interface UserServiceClient {
	@GetMapping("/api/user/user/member-service/{user-no}")
	UserClientUserShortInfoResponse findUserNameByUserId(@PathVariable("user-no") Long userId);
}
