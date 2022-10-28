package com.team6.onandthefarmproductservice.feignclient.service;

import com.team6.onandthefarmproductservice.entity.Product;
import com.team6.onandthefarmproductservice.entity.Review;
import com.team6.onandthefarmproductservice.entity.Wish;
import com.team6.onandthefarmproductservice.feignclient.vo.ProductVo;
import com.team6.onandthefarmproductservice.feignclient.vo.ReviewVo;
import com.team6.onandthefarmproductservice.feignclient.vo.WishListResponse;
import com.team6.onandthefarmproductservice.repository.ProductRepository;
import com.team6.onandthefarmproductservice.repository.ProductWishRepository;
import com.team6.onandthefarmproductservice.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SnsServiceClientServiceImpl implements SnsServiceClientService{

    private final ProductWishRepository productWishRepository;

    private final ProductRepository productRepository;

    private final ReviewRepository reviewRepository;

    @Autowired
    public SnsServiceClientServiceImpl(ProductWishRepository productWishRepository,
                                       ProductRepository productRepository,
                                       ReviewRepository reviewRepository){
        this.productWishRepository = productWishRepository;
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }


    @Override
    public List<WishListResponse> findWishProductListByMember(Long memberId) {

        List<WishListResponse> wishResponseList = new ArrayList<>();

        List<Wish> wishList = productWishRepository.findWishListByUserId(memberId);
        for(Wish wish : wishList){
            WishListResponse response = WishListResponse.builder()
                    .wishId(wish.getWishId())
                    .productId(wish.getProduct().getProductId())
                    .sellerId(wish.getProduct().getSellerId())
                    .productName(wish.getProduct().getProductName())
                    .productPrice(wish.getProduct().getProductPrice())
                    .productMainImgSrc(wish.getProduct().getProductMainImgSrc())
                    .productOriginPlace(wish.getProduct().getProductOriginPlace())
                    .productWishCount(wish.getProduct().getProductWishCount())
                    .productStatus(wish.getProduct().getProductStatus())
                    .build();

            wishResponseList.add(response);
        }

        return wishResponseList;
    }

    @Override
    public List<ProductVo> findBySellerId(Long sellerId) {

        List<ProductVo> productVoList = new ArrayList<>();

        List<Product> productList = productRepository.findProductBySellerId(sellerId);
        for(Product product : productList){
            ProductVo productVo = ProductVo.builder()
                    .productId(product.getProductId())
                    .sellerId(product.getSellerId())
                    .productName(product.getProductName())
                    .productPrice(product.getProductPrice())
                    .productMainImgSrc(product.getProductMainImgSrc())
                    .build();

            productVoList.add(productVo);
        }

        return productVoList;
    }

    @Override
    public List<ReviewVo> findReviewByProductId(Long productId) {

        List<ReviewVo> reviewVoList = new ArrayList<>();

        Optional<Product> product = productRepository.findById(productId);
        List<Review> reviewList = reviewRepository.findReviewByProduct(product.get());
        for(Review review : reviewList){
            ReviewVo reviewVo = ReviewVo.builder()
                    .reviewId(review.getReviewId())
                    .reviewRate(review.getReviewRate())
                    .build();

            reviewVoList.add(reviewVo);
        }

        return reviewVoList;
    }
}
