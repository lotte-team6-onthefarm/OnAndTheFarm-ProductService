package com.team6.onandthefarmproductservice.vo.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductQnAResponse {
    private Long productQnaId;

    private String productQnaContent;

    private String productQnaCreatedAt;

    private String productQnaModifiedAt;

    private String productQnaStatus;

    private String productQnaCategory;

    private String productSellerAnswer;

    private String userName;

    private String userProfileImg;

    private String productName;

    private String productImg;
}
