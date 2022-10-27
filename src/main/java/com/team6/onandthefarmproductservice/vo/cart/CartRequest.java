package com.team6.onandthefarmproductservice.vo.cart;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartRequest {

    private List<CartInfoRequest> cartList;

}