package com.team6.onandthefarmproductservice.vo.product;

import java.util.ArrayList;
import java.util.List;

import com.team6.onandthefarmproductservice.dto.product.ProductImgDto;

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
public class ProductFormRequest {
	private String productName;

	private Long categoryId;

	private Integer productPrice;

	private Integer productTotalStock;

	private String productMainImgSrc;

	private String productDetail;

	private String productOriginPlace;

	private String productDeliveryCompany;

	private String productStatus;

	private String productDetailShort;

	private Integer productWishCount;

	private Integer productSoldCount;

	private List<ProductImgDto> productImgDtoList = new ArrayList<>();

}
