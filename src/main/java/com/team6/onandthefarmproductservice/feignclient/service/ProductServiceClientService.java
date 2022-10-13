package com.team6.onandthefarmproductservice.feignclient.service;

import com.team6.onandthefarmproductservice.feignclient.vo.CartClientResponse;
import com.team6.onandthefarmproductservice.feignclient.vo.ProductClientResponse;

import java.util.List;

public interface ProductServiceClientService {
    List<CartClientResponse> findByUserId(Long userId);

    ProductClientResponse findByProductId(Long productId);
}
