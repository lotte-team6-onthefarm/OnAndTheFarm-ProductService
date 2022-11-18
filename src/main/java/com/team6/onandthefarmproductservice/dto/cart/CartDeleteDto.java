package com.team6.onandthefarmproductservice.dto.cart;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDeleteDto {

    private List<Long> cartList;

    private Long productId;
}
