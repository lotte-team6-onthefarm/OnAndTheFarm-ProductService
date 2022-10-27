package com.team6.onandthefarmproductservice.vo.product;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerProductQnaAnswerRequest {
    private String productQnaId;

    private String productQnaAnswerContent;
}
