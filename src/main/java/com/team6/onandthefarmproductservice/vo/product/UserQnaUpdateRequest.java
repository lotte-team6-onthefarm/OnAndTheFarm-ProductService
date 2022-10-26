package com.team6.onandthefarmproductservice.vo.product;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserQnaUpdateRequest {
    private Long productQnaId;

    private String productQnaContent;
}
