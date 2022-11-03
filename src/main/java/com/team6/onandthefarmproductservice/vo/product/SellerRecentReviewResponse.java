package com.team6.onandthefarmproductservice.vo.product;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerRecentReviewResponse {
    private String reviewContent;

    private Integer reviewLikeCount;

    private Integer reviewRate;

    private String productImg;

    private String productName;

    private Long productId;
}
