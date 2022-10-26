package com.team6.onandthefarmproductservice.feignclient.controller;

import com.team6.onandthefarmproductservice.feignclient.service.ProductServiceClientServiceEX;
import com.team6.onandthefarmproductservice.feignclient.vo.*;
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
    @GetMapping("/api/user/cart/product-service/{user-no}")
    public List<CartVo> findByUserId(@PathVariable("user-no") Long userId){
        return productServiceClientService.findCartByUserId(userId);
    }

    /**
     * 제품ID를 이용해 제품의 정보를 가져오는 것
     * @param productId
     * @return
     */
    @GetMapping("/api/user/product/product-service/{product-no}")
    public ProductVo findByProductId(@PathVariable("product-no") Long productId){
        return productServiceClientService.findByProductId(productId);
    }

    /**
     * 판매하지 않는 상품리스트 조회
     * @param sellerId
     * @return
     */
    @GetMapping("/api/user/product/product-service/no-selling/{seller-no}")
    List<ProductVo> findNotSellingProduct(Long sellerId){
        return productServiceClientService.findNotSellingProduct(sellerId);
    }
    @GetMapping("/api/user/product/product-service/selling/{seller-no}")
    List<ProductVo> findSellingProduct(Long sellerId){
        return productServiceClientService.findSellingProduct(sellerId);
    }

    @GetMapping("/api/user/product/product-service/qna/{seller-no}")
    List<ProductQnaVo> findBeforeAnswerProductQna(Long sellerId){
        return productServiceClientService.findBeforeAnswerProductQna(sellerId);
    }
}
