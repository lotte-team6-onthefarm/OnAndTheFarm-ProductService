package com.team6.onandthefarmproductservice.feignclient.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WishListResponse {

    private Long wishId;

    private Long productId;

    private Long sellerId;

    private String productName;

    private Integer productPrice;

    private String productMainImgSrc;

    private String productOriginPlace;

    private Integer productWishCount;

    private String productStatus;
}
