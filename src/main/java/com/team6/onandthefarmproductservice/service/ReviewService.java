package com.team6.onandthefarmproductservice.service;

import java.util.List;

import com.team6.onandthefarmproductservice.dto.ReviewDeleteDto;
import com.team6.onandthefarmproductservice.dto.ReviewFormDto;
import com.team6.onandthefarmproductservice.dto.ReviewLikeCancelFormDto;
import com.team6.onandthefarmproductservice.dto.ReviewLikeFormDto;
import com.team6.onandthefarmproductservice.dto.ReviewUpdateFormDto;
import com.team6.onandthefarmproductservice.vo.ReviewSelectionResponse;

public interface ReviewService {
	Long saveReview(ReviewFormDto reviewFormDto);
	Long updateReview(ReviewUpdateFormDto reviewUpdateFormDto);
	Long deleteReview(ReviewDeleteDto reviewDeleteDto);
	Long upLikeCountReview(ReviewLikeFormDto reviewLikeFormDto);
	Long cancelReviewLikeCount(ReviewLikeCancelFormDto reviewLikeCancelFormDto);
	List<ReviewSelectionResponse> getReviewListByLikeCount(Long userId, Long productId, Integer pageNumber);
	List<ReviewSelectionResponse> getReviewListOrderByNewest(Long userId, Long productId, Integer pageNumber);
	List<ReviewSelectionResponse> getReviewBySellerNewest(Long sellerId, Integer pageNumber);
	List<ReviewSelectionResponse> getMyReview(Long userId, Integer pageNumber);
}
