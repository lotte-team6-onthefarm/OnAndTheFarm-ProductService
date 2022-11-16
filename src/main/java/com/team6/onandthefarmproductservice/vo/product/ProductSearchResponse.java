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
public class ProductSearchResponse {
	private Long productId;
	private String productName;
	private Integer productPrice;
	private String sellerName;
	private boolean wishStatus;
	private boolean cartStatus;
	private double reviewRate;
	private Integer reviewCount;
	private String imgSrc;
}
