package com.team6.onandthefarmproductservice.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.team6.onandthefarmproductservice.entity.Review;

public interface ReviewPagingRepository extends PagingAndSortingRepository<Review, Long> {
	@Query("select r from Review r join fetch r.product p join fetch p.category join fetch p.seller where r.product.productId =:productId and r.reviewStatus ='created'")
	List<Review> findReviewListByLikeCount(PageRequest pageRequest, @Param("productId") Long productId);

	@Query("select r from Review r join fetch r.product p join fetch p.category join fetch p.seller where r.product.productId =:productId and r.reviewStatus ='created'")
	List<Review> findReviewListByNewest(PageRequest pageRequest, @Param("productId") Long productId);

	@Query("select r from Review r join fetch r.seller s where r.seller.sellerId =:sellerId and r.reviewStatus ='created'")
	List<Review> findReviewListBySeller(PageRequest pageRequest, @Param("sellerId") Long sellerId);

	@Query("select r from Review r join fetch r.user u where r.user.userId =:userId and r.reviewStatus ='created'")
	List<Review> findReviewListByUser(PageRequest pageRequest, @Param("userId") Long userId);
}
