package com.team6.onandthefarmproductservice.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductWishResponse {

    private Long wistId;
    private Long productId;
    private String productName;
    private Integer productPrice;
    private String productMainImgSrc;
    private String productDetail;
    private String productOriginPlace;
    private String productDetailShort;

}
