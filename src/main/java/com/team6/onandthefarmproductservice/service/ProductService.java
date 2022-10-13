package com.team6.onandthefarmproductservice.service;

import java.util.List;

import com.team6.onandthefarmproductservice.dto.ProductDeleteDto;
import com.team6.onandthefarmproductservice.dto.ProductFormDto;
import com.team6.onandthefarmproductservice.dto.ProductUpdateFormDto;
import com.team6.onandthefarmproductservice.dto.ProductWishCancelDto;
import com.team6.onandthefarmproductservice.dto.ProductWishFormDto;
import com.team6.onandthefarmproductservice.dto.ProductWishResultDto;
import com.team6.onandthefarmproductservice.vo.ProductDetailResponse;
import com.team6.onandthefarmproductservice.vo.ProductQnAResponse;
import com.team6.onandthefarmproductservice.vo.ProductReviewResponse;
import com.team6.onandthefarmproductservice.vo.ProductSelectionResponse;
import com.team6.onandthefarmproductservice.vo.ProductWishResponse;

public interface ProductService {
	Long saveProduct(Long sellerId, ProductFormDto productFormDto);
	
    Long updateProduct(ProductUpdateFormDto productUpdateFormDto);

    Long deleteProduct(ProductDeleteDto productDeleteDto);

    ProductWishResultDto addProductToWishList(ProductWishFormDto productWishFormDto);

    List<Long> cancelProductFromWishList(ProductWishCancelDto productWishCancelDto);

    List<ProductWishResponse> getWishList(Long userId);

    List<ProductSelectionResponse> getAllProductListOrderByNewest(Long userId, Long sellerId, Integer pageNumber);

    List<ProductSelectionResponse> getProductsListByHighPrice(Long userId, Long sellerId, Integer pageNumber);

    List<ProductSelectionResponse> getProductsListByLowPrice(Long userId, Long sellerId, Integer pageNumber);

    List<ProductSelectionResponse> getProductsBySoldCount(Long userId, Long sellerId, Integer pageNumber);

    List<ProductSelectionResponse> getProductListByCategoryNewest(Long userId, Long sellerId, String Category, Integer pageNumber);

    List<ProductSelectionResponse> getProductListByCategoryHighest(Long userId, Long sellerId, String Category, Integer pageNumber);

    List<ProductSelectionResponse> getProductListByCategoryLowest(Long userId, Long sellerId, String category, Integer pageNumber);

    List<ProductSelectionResponse> getProductsByCategorySoldCount(Long userId, Long sellerId, String category, Integer pageNumber);

    List<ProductSelectionResponse> getProductListBySellerNewest(Long userId, Long SellerId, Integer pageNumber);

    List<ProductQnAResponse> findProductQnAList(Long productId);

	List<ProductReviewResponse> getProductsWithoutReview(Long userId);
	
    ProductDetailResponse findProductDetail(Long productId, Long userId);
}