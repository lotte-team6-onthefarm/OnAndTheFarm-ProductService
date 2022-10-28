package com.team6.onandthefarmproductservice.vo.product;

import lombok.Data;

import java.util.List;

@Data
public class ProductReviewResult {

    List<ProductReviewResponse> productReviewResponseList;

    private Integer currentPageNum;

    private Integer totalPageNum;

    private Integer totalElementNum;
}
