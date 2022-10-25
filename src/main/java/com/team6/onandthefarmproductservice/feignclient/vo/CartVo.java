package com.team6.onandthefarmproductservice.feignclient.vo;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartVo {

    private Long cartId;

    private Long productId;

    private Long userId;

    private Integer cartQty;

    private Boolean cartIsActivated;

    private Boolean cartStatus;

    private String cartCreatedAt;
}
