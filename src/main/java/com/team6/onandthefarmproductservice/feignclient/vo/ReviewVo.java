package com.team6.onandthefarmproductservice.feignclient.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewVo {

    private Long reviewId;

    private Integer reviewRate;

}
