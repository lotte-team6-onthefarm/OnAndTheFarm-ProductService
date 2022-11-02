package com.team6.onandthefarmproductservice.vo.product;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductQnAResponseResult {
    private List<ProductQnAResponse> productQnAResponseList;

    private Integer currentPageNum;

    private Integer totalPageNum;
}
