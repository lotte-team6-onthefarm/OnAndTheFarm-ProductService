package com.team6.onandthefarmproductservice.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImgDto {
	private Long productId;
	private String productImgSrc;
}