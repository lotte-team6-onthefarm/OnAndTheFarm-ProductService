package com.team6.onandthefarmproductservice.feignclient;

import com.team6.onandthefarmproductservice.feignclient.vo.UserVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.team6.onandthefarmproductservice.vo.product.UserClientUserShortInfoResponse;

@FeignClient(name = "member-service", contextId = "member-service-user")
public interface UserServiceClient {

	@GetMapping("/api/feign/user/members/member-service/short-info/{user-no}")
	UserClientUserShortInfoResponse findUserNameByUserId(@PathVariable("user-no") Long userId);

	@GetMapping("/api/feign/user/members/member-service/{user-no}")
	UserVo findByUserId(@PathVariable("user-no") Long userId);
}
