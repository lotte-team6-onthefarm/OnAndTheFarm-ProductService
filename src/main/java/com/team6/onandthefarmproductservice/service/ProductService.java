package com.team6.onandthefarmproductservice.service;

import java.io.IOException;
import java.util.List;

import com.team6.onandthefarmproductservice.dto.product.*;
import com.team6.onandthefarmproductservice.vo.product.*;

public interface ProductService {
	Long saveProduct(ProductFormDto productFormDto) throws IOException;
	
    Long updateProduct(ProductUpdateFormDto productUpdateFormDto) throws IOException;

    Long deleteProduct(ProductDeleteDto productDeleteDto);

    ProductWishResultDto addProductToWishList(ProductWishFormDto productWishFormDto);

    List<Long> cancelProductFromWishList(ProductWishCancelDto productWishCancelDto);

    ProductWishResult getWishList(Long userId, Integer pageNumber);

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

    ProductReviewResult getProductsWithoutReview(Long userId, Integer pageNumber);
	
    ProductDetailResponse findProductDetail(Long productId, Long userId);

    void updateStockAndSoldCount(Object productStockDto);

    Boolean createProductQnA(UserQnaDto userQnaDto);

    ProductQnAResultResponse findUserQna(Long userId, Integer pageNum);

    Boolean updateUserQna(UserQnaUpdateDto userQnaUpdateDto);

    Boolean deleteUserQna(Long productQnaId);

    List<SellerRecentReviewResponse> findReviewMypage(Long sellerId);

    List<SellerPopularProductResponse> findPopularProduct(Long sellerId);

    SellerMypageResponse findSellerMypage(SellerMypageDto sellerMypageDto);

    SellerProductQnaResponseResult findSellerQnA(Long sellerId, Integer pageNumber);

    Boolean createQnaAnswer(SellerQnaDto sellerQnaDto);
}