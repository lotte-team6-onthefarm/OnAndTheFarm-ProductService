package com.team6.onandthefarmproductservice.vo.cart;

import lombok.Data;

import java.util.List;

@Data
public class CartResult {

    private List<CartResponse> cartResponseList;

    private Integer currentPageNum;

    private Integer totalPageNum;

    private Integer totalElementNum;

}