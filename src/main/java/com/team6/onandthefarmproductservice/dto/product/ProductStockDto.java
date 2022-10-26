package com.team6.onandthefarmproductservice.dto.product;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockDto {
    private Long productId;

    private Integer productQty;
}
