package com.team6.onandthefarmproductservice.feignclient.vo;

import com.team6.onandthefarmproductservice.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartClientResponse {
    private Long cartId;

    private Long productId;

    private Long userId;

    private Integer cartQty;

    private Boolean cartIsActivated;

    private Boolean cartStatus;

    private String cartCreatedAt;
}
