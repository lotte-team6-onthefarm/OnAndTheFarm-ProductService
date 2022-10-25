package com.team6.onandthefarmproductservice.feignclient.controller;

import com.team6.onandthefarmproductservice.feignclient.service.ProductServiceClientServiceEX;
import com.team6.onandthefarmproductservice.feignclient.vo.CartClientResponse;
import com.team6.onandthefarmproductservice.feignclient.vo.ProductClientResponse;
import com.team6.onandthefarmproductservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductServiceClientControllerEX {

    private final ProductServiceClientServiceEX productServiceClientService;

    /**
     * 유저ID로 유저의 장바구니 목록을 가져오는 것
     * @param userId
     * @return
     */
    @GetMapping("/api/user/product-service/cart/{user-no}")
    public List<CartClientResponse> findByUserId(@PathVariable("user-no") Long userId){
        return productServiceClientService.findByUserId(userId);
    }

    /**
     * 제품ID를 이용해 제품의 정보를 가져오는 것
     * @param productId
     * @return
     */
    @GetMapping("/api/user/product-service/product/{product-no}")
    public ProductClientResponse findByProductId(@PathVariable("product-no") Long productId){
        return productServiceClientService.findByProductId(productId);
    }
}
