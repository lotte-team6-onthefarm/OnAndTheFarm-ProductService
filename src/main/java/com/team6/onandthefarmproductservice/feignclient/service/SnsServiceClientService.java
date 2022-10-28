package com.team6.onandthefarmproductservice.feignclient.service;

import com.team6.onandthefarmproductservice.feignclient.vo.ProductVo;
import com.team6.onandthefarmproductservice.feignclient.vo.ReviewVo;
import com.team6.onandthefarmproductservice.feignclient.vo.WishListResponse;

import java.util.List;

public interface SnsServiceClientService {

    List<WishListResponse> findWishProductListByMember(Long memberId);

    List<ProductVo> findBySellerId(Long sellerId);

    List<ReviewVo> findReviewByProductId(Long productId);
}
