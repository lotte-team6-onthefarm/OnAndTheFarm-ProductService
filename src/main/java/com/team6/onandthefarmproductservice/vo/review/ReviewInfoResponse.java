package com.team6.onandthefarmproductservice.vo.review;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewInfoResponse {

    private Integer reviewCount;
    private Double reviewRate;
    private Integer reviewFiveCount;
    private Integer reviewFourCount;
    private Integer reviewThreeCount;
    private Integer reviewTwoCount;
    private Integer reviewOneCount;
}
