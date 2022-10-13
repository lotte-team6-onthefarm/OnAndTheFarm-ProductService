package com.team6.onandthefarmproductservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.team6.onandthefarmproductservice.entity.ReviewLike;

public interface ReviewLikeRepository extends CrudRepository<ReviewLike, Long> {
    @Query("select r from ReviewLike r where r.review.reviewId =:reviewId and r.userId =:userId")
    Optional<ReviewLike> findReviewLikeByUser(@Param("userId")Long userId, @Param("reviewId")Long reviewId);
}
