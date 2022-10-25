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
public class ProductQnaAnswerResponse {
    private String productQnaAnswerContent;

    private String productQnaAnswerCreatedAt;

    private String productQnaAnswerModifiedAt;
}
