package com.team6.onandthefarmproductservice.dto.product;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchProductVoDto {
	private String searchProduct;
	private Integer pageNumber;
}
