package com.team6.onandthefarmproductservice.dto.cart;

import com.team6.onandthefarmproductservice.vo.cart.CartInfoRequest;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {

    private List<CartInfoRequest> cartList;

}
