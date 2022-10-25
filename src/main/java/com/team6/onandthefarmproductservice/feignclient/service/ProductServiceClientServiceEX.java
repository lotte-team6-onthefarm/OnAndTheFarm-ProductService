package com.team6.onandthefarmproductservice.feignclient.service;

import com.team6.onandthefarmproductservice.feignclient.vo.*;

import java.util.List;

public interface ProductServiceClientServiceEX {
    List<CartVo> findCartByUserId(Long userId);

    ProductVo findByProductId(Long productId);

    List<ProductVo> findNotSellingProduct(Long sellerId);

    List<ProductVo> findSellingProduct(Long sellerId);

    List<ProductQnaVo> findBeforeAnswerProductQna(Long sellerId);
}