package com.team6.onandthefarmproductservice.vo.product;

import com.team6.onandthefarmproductservice.entity.Product;
import com.team6.onandthefarmproductservice.vo.PageVo;

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
public class ProductSelectionResponse {
	private Long productId;
	private Long categoryId;
	private String categoryName;
	private Long sellerId;
	private String sellerEmail;
	private String sellerAddress;
	private String sellerAddressDetail;
	private String sellerPhone;
	private String sellerName;
	private String sellerShopName;
	private String sellerBusinessNumber;
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
	private Integer productReviewCount;
	private Boolean productCartStatus;
	private Boolean productWishStatus;
	private Double reviewRate;

	public ProductSelectionResponse(Product product, SellerClientSellerDetailResponse sellerClientSellerDetailResponse){
		this.productId = product.getProductId();
		this.categoryId = product.getCategory().getCategoryId();
		this.categoryName = product.getCategory().getCategoryName();
		this.sellerId = sellerClientSellerDetailResponse.getSellerId();
		this.sellerEmail = sellerClientSellerDetailResponse.getSellerEmail();
		this.sellerAddress = sellerClientSellerDetailResponse.getSellerAddress();
		this.sellerAddressDetail = sellerClientSellerDetailResponse.getSellerAddressDetail();
		this.sellerPhone = sellerClientSellerDetailResponse.getSellerPhone();
		this.sellerName = sellerClientSellerDetailResponse.getSellerName();
		this.sellerShopName = sellerClientSellerDetailResponse.getSellerShopName();
		this.sellerBusinessNumber = sellerClientSellerDetailResponse.getSellerBusinessNumber();
		this.productName = product.getProductName();
		this.productPrice = product.getProductPrice();
		this.productTotalStock = product.getProductTotalStock();
		this.productMainImgSrc = product.getProductMainImgSrc();
		this.productDetail = product.getProductDetail();
		this.productDetailShort = product.getProductDetailShort();
		this.productOriginPlace = product.getProductOriginPlace();
		this.productDeliveryCompany = product.getProductDeliveryCompany();
		this.productRegisterDate = product.getProductRegisterDate();
		this.productUpdateDate = product.getProductUpdateDate();
		this.productStatus = product.getProductStatus();
		this.productWishCount = product.getProductWishCount();
		this.productSoldCount = product.getProductSoldCount();
		this.productViewCount = product.getProductViewCount();
		this.productWishStatus = false;
		this.productCartStatus = false;
	}
}
