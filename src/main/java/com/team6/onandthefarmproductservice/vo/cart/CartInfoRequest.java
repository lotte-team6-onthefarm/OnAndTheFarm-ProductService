package com.team6.onandthefarmproductservice.vo.cart;

import lombok.Data;

@Data
public class CartInfoRequest {

    private Long productId;
    private Integer cartQty;

}
