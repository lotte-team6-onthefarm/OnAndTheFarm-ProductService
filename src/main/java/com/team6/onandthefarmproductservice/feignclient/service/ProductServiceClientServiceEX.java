package com.team6.onandthefarmproductservice.feignclient.service;

import com.team6.onandthefarmproductservice.entity.Product;
import com.team6.onandthefarmproductservice.entity.ReservedOrder;
import com.team6.onandthefarmproductservice.entity.Wish;
import com.team6.onandthefarmproductservice.feignclient.vo.*;
import com.team6.onandthefarmproductservice.vo.WishVo;
import com.team6.onandthefarmproductservice.vo.product.WishPageVo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ProductServiceClientServiceEX {
    List<CartVo> findCartByUserId(Long userId);

    ProductVo findByProductId(Long productId);

    List<ProductVo> findNotSellingProduct(Long sellerId);

    List<ProductVo> findSellingProduct(Long sellerId);

    List<ProductQnaVo> findBeforeAnswerProductQna(Long sellerId);

    ReservedOrder reservedOrder(String productList,String orderSerial);

    Boolean confirmOrder(Long id);

    void cancelOrder(Long id);

    List<WishVo> getWishListByMemberId(WishPageVo wishPageVo, Long memberId);

    ProductVo getProductVoByProductId(Long productId);
}
