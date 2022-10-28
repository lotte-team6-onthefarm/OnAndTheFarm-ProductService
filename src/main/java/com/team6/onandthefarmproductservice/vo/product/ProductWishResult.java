package com.team6.onandthefarmproductservice.vo.product;

import lombok.Data;

import java.util.List;

@Data
public class ProductWishResult {

    private List<ProductWishResponse> productWishResponseList;

    private Integer currentPageNum;

    private Integer totalPageNum;

    private Integer totalElementNum;
}
