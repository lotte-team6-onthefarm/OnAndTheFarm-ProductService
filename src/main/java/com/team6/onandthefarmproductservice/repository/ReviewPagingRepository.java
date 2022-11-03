package com.team6.onandthefarmproductservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.team6.onandthefarmproductservice.entity.Review;

public interface ReviewPagingRepository extends PagingAndSortingRepository<Review, Long> {
	@Query(value = "select r from Review r join fetch r.product p join fetch p.category join fetch p.seller where r.product.productId =:productId and r.reviewStatus ='created'",
			countQuery = "select count(r) from Review r where r.product.productId =:productId and r.reviewStatus ='created'")
	Page<Review> findReviewListOrderBy(PageRequest pageRequest, @Param("productId") Long productId);

	@Query(value = "select r from Review r join fetch r.seller s where r.seller.sellerId =:sellerId and r.reviewStatus ='created'",
			countQuery = "select count(r) from Review r where r.seller.sellerId =:sellerId and r.reviewStatus ='created'")
	Page<Review> findReviewListBySeller(PageRequest pageRequest, @Param("sellerId") Long sellerId);

	@Query(value = "select r from Review r join fetch r.user u where r.user.userId =:userId and r.reviewStatus ='created'",
			countQuery = "select count(r) from Review r where r.user.userId =:userId and r.reviewStatus ='created'")
	Page<Review> findReviewListByUser(PageRequest pageRequest, @Param("userId") Long userId);
}
