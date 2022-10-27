package com.team6.onandthefarmproductservice.vo.cart;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartIsActivatedRequest {

    private Long cartId;
    private Boolean cartIsActivated;
}
