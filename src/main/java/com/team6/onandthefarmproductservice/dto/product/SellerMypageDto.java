package com.team6.onandthefarmproductservice.dto.product;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SellerMypageDto {
    private Long sellerId;

    private String startDate;

    private String endDate;
}
