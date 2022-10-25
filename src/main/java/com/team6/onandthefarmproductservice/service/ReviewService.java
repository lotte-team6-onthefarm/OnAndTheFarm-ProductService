package com.team6.onandthefarmproductservice.service;

import java.util.List;

import com.team6.onandthefarmproductservice.dto.review.ReviewDeleteDto;
import com.team6.onandthefarmproductservice.dto.review.ReviewFormDto;
import com.team6.onandthefarmproductservice.dto.review.ReviewLikeCancelFormDto;
import com.team6.onandthefarmproductservice.dto.review.ReviewLikeFormDto;
import com.team6.onandthefarmproductservice.dto.review.ReviewUpdateFormDto;
import com.team6.onandthefarmproductservice.vo.review.ReviewInfoResponse;
import com.team6.onandthefarmproductservice.vo.review.ReviewSelectionResponse;

public interface ReviewService {
	Long saveReview(ReviewFormDto reviewFormDto);
	Long updateReview(ReviewUpdateFormDto reviewUpdateFormDto);
	Long deleteReview(ReviewDeleteDto reviewDeleteDto);
	Long upLikeCountReview(ReviewLikeFormDto reviewLikeFormDto);
	Long cancelReviewLikeCount(ReviewLikeCancelFormDto reviewLikeCancelFormDto);
	ReviewInfoResponse getReviewInfo(Long productId);
	List<ReviewSelectionResponse> getReviewListByLikeCount(Long userId, Long productId, Integer pageNumber);
	List<ReviewSelectionResponse> getReviewListOrderByNewest(Long userId, Long productId, Integer pageNumber);
	List<ReviewSelectionResponse> getReviewBySellerNewest(Long sellerId, Integer pageNumber);
	List<ReviewSelectionResponse> getMyReview(Long userId, Integer pageNumber);
}
