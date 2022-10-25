package com.team6.onandthefarmproductservice.vo.product;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductQnAInfoResponse {

    private List<ProductQnAResponse> productQnAResponseList;
    private Integer qnACount;

}
