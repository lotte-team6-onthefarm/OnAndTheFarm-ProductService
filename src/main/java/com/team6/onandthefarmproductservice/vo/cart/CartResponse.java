package com.team6.onandthefarmproductservice.vo.cart;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Long cartId;
    private Integer cartQty;
    private Boolean cartIsActivated;

    private Long productId;
    private String productName;
    private Integer productPrice;
    private String productMainImgSrc;
    private String productOriginPlace;
    private String productDeliveryCompany;
    private String productStatus;

}
