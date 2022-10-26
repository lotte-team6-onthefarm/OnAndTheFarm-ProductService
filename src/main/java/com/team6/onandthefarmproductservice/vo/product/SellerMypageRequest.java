package com.team6.onandthefarmproductservice.vo.product;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerMypageRequest {
    private String startDate;

    private String endDate;
}
