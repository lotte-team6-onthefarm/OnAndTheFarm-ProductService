package com.team6.onandthefarmproductservice.vo.product;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SellerProductQnaResponseResult {
    private List<SellerProductQnaResponse> sellerProductQnaResponseList;

    private Integer currentPageNum;

    private Integer totalPageNum;
}
