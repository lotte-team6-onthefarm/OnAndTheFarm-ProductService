package com.team6.onandthefarmproductservice.vo;

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
public class ProductVo {

	private Long productId;

	private Long categoryId;

	private Long sellerId;

	private String productName;

	private Integer productPrice;

	private Integer productTotalStock;

	private String productMainImgSrc;

	private String productDetail;

	private String productDetailShort;

	private String productOriginPlace;

	private String productDeliveryCompany;

	private String productRegisterDate;

	private String productUpdateDate;

	private String productStatus;

	private Integer productWishCount;

	private Integer productSoldCount;

	private Integer productViewCount;
}
