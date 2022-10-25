package com.team6.onandthefarmproductservice.service;

import java.io.IOException;
import java.util.List;

import com.team6.onandthefarmproductservice.dto.product.ProductDeleteDto;
import com.team6.onandthefarmproductservice.dto.product.ProductFormDto;
import com.team6.onandthefarmproductservice.dto.product.ProductUpdateFormDto;
import com.team6.onandthefarmproductservice.dto.product.ProductWishCancelDto;
import com.team6.onandthefarmproductservice.dto.product.ProductWishFormDto;
import com.team6.onandthefarmproductservice.dto.product.ProductWishResultDto;
import com.team6.onandthefarmproductservice.vo.product.ProductDetailResponse;
import com.team6.onandthefarmproductservice.vo.product.ProductQnAResponse;
import com.team6.onandthefarmproductservice.vo.product.ProductReviewResponse;
import com.team6.onandthefarmproductservice.vo.product.ProductSelectionResponse;
import com.team6.onandthefarmproductservice.vo.product.ProductWishResponse;

public interface ProductService {
	Long saveProduct(ProductFormDto productFormDto) throws IOException;
	
    Long updateProduct(ProductUpdateFormDto productUpdateFormDto) throws IOException;

    Long deleteProduct(ProductDeleteDto productDeleteDto);

    ProductWishResultDto addProductToWishList(ProductWishFormDto productWishFormDto);

    List<Long> cancelProductFromWishList(ProductWishCancelDto productWishCancelDto);

    List<ProductWishResponse> getWishList(Long userId);

    List<ProductSelectionResponse> getAllProductListOrderByNewest(Long userId, Integer pageNumber);

    List<ProductSelectionResponse> getProductsListByHighPrice(Long userId, Integer pageNumber);

    List<ProductSelectionResponse> getProductsListByLowPrice(Long userId, Integer pageNumber);

    List<ProductSelectionResponse> getMainProductsBySoldCount(Long userId);

    List<ProductSelectionResponse> getProductsBySoldCount(Long userId, Integer pageNumber);

    List<ProductSelectionResponse> getProductListByCategoryNewest(Long userId, String Category, Integer pageNumber);

    List<ProductSelectionResponse> getProductListByCategoryHighest(Long userId, String Category, Integer pageNumber);

    List<ProductSelectionResponse> getProductListByCategoryLowest(Long userId, String category, Integer pageNumber);

    List<ProductSelectionResponse> getProductsByCategorySoldCount(Long userId, String category, Integer pageNumber);

    List<ProductSelectionResponse> getProductListBySellerNewest(Long userId, Long SellerId, Integer pageNumber);

    List<ProductSelectionResponse> getSellingProductListBySellerNewest(Long userId, Long sellerId, Integer pageNumber);

    List<ProductSelectionResponse> getPauseProductListBySellerNewest(Long userId, Long sellerId, Integer pageNumber);

    List<ProductQnAResponse> findProductQnAList(Long productId);

	List<ProductReviewResponse> getProductsWithoutReview(Long userId);
	
    ProductDetailResponse findProductDetail(Long productId, Long userId);
}