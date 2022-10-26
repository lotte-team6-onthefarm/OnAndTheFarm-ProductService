package com.team6.onandthefarmproductservice.vo.product;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserQnaRequest {
    private Long productId;

    private String productQnaContent;

}
