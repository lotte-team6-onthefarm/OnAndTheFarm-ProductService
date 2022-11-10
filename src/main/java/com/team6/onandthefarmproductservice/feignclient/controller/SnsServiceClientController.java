package com.team6.onandthefarmproductservice.feignclient.controller;

import com.team6.onandthefarmproductservice.feignclient.service.SnsServiceClientService;
import com.team6.onandthefarmproductservice.feignclient.vo.ProductVo;
import com.team6.onandthefarmproductservice.feignclient.vo.ReviewVo;
import com.team6.onandthefarmproductservice.feignclient.vo.WishListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SnsServiceClientController {

    private final SnsServiceClientService snsServiceClientService;

    @Autowired
    public SnsServiceClientController(SnsServiceClientService snsServiceClientService){
        this.snsServiceClientService = snsServiceClientService;
    }

    @GetMapping("/api/feign/user/product/product-service/wish/{user-no}")
    public List<WishListResponse> findWishProductListByMember(@PathVariable("user-no") Long memberId){
        return snsServiceClientService.findWishProductListByMember(memberId);
    }

    @GetMapping("/api/feign/seller/product/product-service/product/list/{seller-no}")
    List<ProductVo> findBySellerId(@PathVariable("seller-no") Long sellerId){
        return snsServiceClientService.findBySellerId(sellerId);
    }

    @GetMapping("/api/feign/user/product/product-service/review/{product-no}")
    List<ReviewVo> findReviewByProductId(@PathVariable("product-no") Long productId){
        return snsServiceClientService.findReviewByProductId(productId);
    }
}
