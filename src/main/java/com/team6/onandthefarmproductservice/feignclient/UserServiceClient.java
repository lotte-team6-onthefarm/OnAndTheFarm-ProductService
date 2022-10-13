package com.team6.onandthefarmproductservice.feignclient;



import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.team6.onandthefarmproductservice.vo.UserClientUserShortInfo;

@FeignClient(name = "member-service")
public interface UserServiceClient {
	@GetMapping("/api/user/member-service/username/{user-no}")
	UserClientUserShortInfo findUserNameByUserId(@PathVariable("user-no") Long userId);
}
