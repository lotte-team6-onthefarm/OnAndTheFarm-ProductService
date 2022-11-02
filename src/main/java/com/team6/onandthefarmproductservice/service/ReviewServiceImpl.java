package com.team6.onandthefarmproductservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.team6.onandthefarmproductservice.vo.product.UserClientUserShortInfoResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team6.onandthefarmproductservice.dto.review.ReviewDeleteDto;
import com.team6.onandthefarmproductservice.dto.review.ReviewFormDto;
import com.team6.onandthefarmproductservice.dto.review.ReviewLikeCancelFormDto;
import com.team6.onandthefarmproductservice.dto.review.ReviewLikeFormDto;
import com.team6.onandthefarmproductservice.dto.review.ReviewUpdateFormDto;
import com.team6.onandthefarmproductservice.entity.Product;
import com.team6.onandthefarmproductservice.entity.Review;
import com.team6.onandthefarmproductservice.entity.ReviewLike;
import com.team6.onandthefarmproductservice.feignclient.OrderServiceClient;
import com.team6.onandthefarmproductservice.feignclient.UserServiceClient;
import com.team6.onandthefarmproductservice.repository.ProductRepository;
import com.team6.onandthefarmproductservice.repository.ReviewLikeRepository;
import com.team6.onandthefarmproductservice.repository.ReviewPagingRepository;
import com.team6.onandthefarmproductservice.repository.ReviewRepository;
import com.team6.onandthefarmproductservice.util.DateUtils;
import com.team6.onandthefarmproductservice.vo.PageVo;
import com.team6.onandthefarmproductservice.vo.order.OrderClientOrderProductIdResponse;
import com.team6.onandthefarmproductservice.vo.review.ReviewInfoResponse;
import com.team6.onandthefarmproductservice.vo.review.ReviewSelectionResponse;
import com.team6.onandthefarmproductservice.vo.review.ReviewSelectionResponseResult;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final ReviewPagingRepository reviewPagingRepository;
	private final ReviewLikeRepository reviewLikeRepository;
	private final ProductRepository productRepository;
	private final OrderServiceClient orderServiceClient;
	private final UserServiceClient userServiceClient;

	private final DateUtils dateUtils;
	private final Environment env;

	private final CircuitBreakerFactory circuitBreakerFactory;

	@Autowired ReviewServiceImpl(ReviewRepository reviewRepository, ReviewPagingRepository reviewPagingRepository,
								 ReviewLikeRepository reviewLikeRepository,
								 ProductRepository productRepository,
								 OrderServiceClient orderServiceClient,
								 UserServiceClient userServiceClient,
								 DateUtils dateUtils, Environment env,
								 CircuitBreakerFactory circuitBreakerFactory){
		this.reviewRepository = reviewRepository;
		this.reviewPagingRepository = reviewPagingRepository;
		this.reviewLikeRepository = reviewLikeRepository;
		this.productRepository = productRepository;
		this.orderServiceClient = orderServiceClient;
		this.userServiceClient = userServiceClient;
		this.dateUtils = dateUtils;
		this.env = env;
		this.circuitBreakerFactory=circuitBreakerFactory;
	}

	public Long saveReview(ReviewFormDto reviewFormDto){
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("order_circuitbreaker");

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		Review review = modelMapper.map(reviewFormDto, Review.class);

		// Optional<OrderProduct> orderProduct = orderProductRepository.findById(reviewFormDto.getOrderProductId());
		OrderClientOrderProductIdResponse orderClientOrderProductIdResponse
				= circuitBreaker.run(
						()->orderServiceClient
								.findProductIdByOrderProductId(reviewFormDto.getOrderProductId()),
				throwable -> new OrderClientOrderProductIdResponse());
		//OrderClientOrderProductIdResponse orderClientOrderProductIdResponse = orderServiceClient.findProductIdByOrderProductId(reviewFormDto.getOrderProductId());
		Long productId = orderClientOrderProductIdResponse.getProductId();
		Optional<Product> product = productRepository.findById(productId);
		Long userId = reviewFormDto.getUserId();
		Long sellerId = product.get().getSellerId();

		review.setSellerId(sellerId);
		review.setProduct(product.get());
		review.setUserId(userId);
		review.setReviewCreatedAt((dateUtils.transDate(env.getProperty("dateutils.format"))));
		review.setReviewLikeCount(0);
		review.setReviewStatus("created");

		return reviewRepository.save(review).getReviewId();
	}

	public Long updateReview(ReviewUpdateFormDto reviewUpdateFormDto){
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		Optional<Review> review = reviewRepository.findById(reviewUpdateFormDto.getReviewId());
		if(review.get().getReviewStatus().equals("deleted")) return 0l;
		long reviewId = review.get().updateReview(reviewUpdateFormDto.getReviewContent(), reviewUpdateFormDto.getReviewRate());

		review.get().setReviewModifiedAt(dateUtils.transDate(env.getProperty("dateutils.format")));

		return reviewId;
	}

	public Long deleteReview(ReviewDeleteDto reviewDeleteDto){
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		Optional<Review> review = reviewRepository.findById(reviewDeleteDto.getReviewId());
		//product.get().setProductStatus("deleted");
		review.get().setReviewStatus("deleted");
		review.get().setReviewModifiedAt(dateUtils.transDate(env.getProperty("dateutils.format")));

		return review.get().getReviewId();
	}


	public Long upLikeCountReview(ReviewLikeFormDto reviewLikeFormDto){
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		Optional<ReviewLike> savedReviewLike = reviewLikeRepository.findReviewLikeByUser(reviewLikeFormDto.getUserId(), reviewLikeFormDto.getReviewId());

		Long reviewLikeId = null;
		if(savedReviewLike.isPresent()){
			reviewLikeId = savedReviewLike.get().getReviewLikeId();
		}
		else {
			ReviewLike reviewLike = modelMapper.map(reviewLikeFormDto, ReviewLike.class);

			Optional<Review> review = reviewRepository.findById(reviewLikeFormDto.getReviewId());
			review.get().setReviewLikeCount(review.get().getReviewLikeCount() + 1);
			reviewLike.setReview(review.get());
			reviewLike.setUserId(reviewLike.getUserId());

			reviewLikeId = reviewLikeRepository.save(reviewLike).getReviewLikeId();
		}

		return reviewLikeId;
	}

	public Long cancelReviewLikeCount(ReviewLikeCancelFormDto reviewLikeCancelFormDto){
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);


		Long reviewId = reviewLikeCancelFormDto.getReviewId();
		Long userId = reviewLikeCancelFormDto.getUserId();
		ReviewLike reviewLike = reviewLikeRepository.findReviewLikeByUser(userId, reviewId).get();
		Long reviewLikeId = reviewLike.getReviewLikeId();

		reviewLikeRepository.delete(reviewLike);
		Optional<Review> review = reviewRepository.findById(reviewLikeCancelFormDto.getReviewId());
		review.get().setReviewLikeCount(review.get().getReviewLikeCount() - 1);

		return reviewLikeId;
	}

	@Override
	public ReviewInfoResponse getReviewInfo(Long productId) {

		Optional<Product> product = productRepository.findById(productId);

		int totalCountOfReview = 0;
		int sumOfReview = 0;
		Integer[] reviewRates = new Integer[6];
		for(int i=1; i<=5; i++) {
			List<Review> reviewListByReviewRate = reviewRepository.findReviewByProductAndReviewRate(product.get(), i);
			reviewRates[i] = reviewListByReviewRate.size();
			totalCountOfReview += reviewListByReviewRate.size();

			sumOfReview += (reviewListByReviewRate.size() * i);
		}

		Double avgOfReview = 0.0;
		if(totalCountOfReview > 0){
			avgOfReview = ((double)sumOfReview/totalCountOfReview);
		}
		ReviewInfoResponse reviewInfoResponse = ReviewInfoResponse.builder()
				.reviewCount(totalCountOfReview)
				.reviewRate(avgOfReview)
				.reviewFiveCount(reviewRates[5])
				.reviewFourCount(reviewRates[4])
				.reviewThreeCount(reviewRates[3])
				.reviewTwoCount(reviewRates[2])
				.reviewOneCount(reviewRates[1])
				.build();

		return reviewInfoResponse;
	}


	public ReviewSelectionResponseResult getReviewListByLikeCount(Long userId, Long productId, Integer pageNumber){
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("user_circuitbreaker");

		// msa 고려하여 다시 설계할 것
		// Product product = productRepository.findById(productId).get();
		// List<Review> reviews = reviewRepository.findReviewsByProductOrderByReviewLikeCountDesc(product);
		List<ReviewSelectionResponse> reviewResponses = new ArrayList<>();
		PageRequest pageRequest = PageRequest.of(pageNumber, 8, Sort.by("reviewLikeCount").descending());
		Page<Review> reviews = reviewPagingRepository.findReviewListByLikeCount(pageRequest, productId);
		for (Review review : reviews) {
			UserClientUserShortInfoResponse user
					= circuitBreaker.run(
							()->userServiceClient.findUserNameByUserId(review.getUserId()),
					throwable -> new UserClientUserShortInfoResponse());
			ReviewSelectionResponse reviewSelectionResponse = ReviewSelectionResponse
					.builder()
					.reviewId(review.getReviewId())
					.reviewContent(review.getReviewContent())
					.reviewCreatedAt(review.getReviewCreatedAt())
					.reviewModifiedAt(review.getReviewModifiedAt())
					.reviewLikeCount(review.getReviewLikeCount())
					.reviewRate(review.getReviewRate())
					.userName(user.getUserName())
					.userProfileImg(user.getUserProfileImg())
					.isMyReview(false)
					.isAvailableUp(true)
					.build();

			if(review.getUserId().equals(userId)){
				reviewSelectionResponse.setIsMyReview(true);
			}
			Optional<ReviewLike> reviewLike = reviewLikeRepository.findReviewLikeByUser(userId, review.getReviewId());
			if(reviewLike.isPresent()){
				reviewSelectionResponse.setIsAvailableUp(false);
			}
			reviewResponses.add(reviewSelectionResponse);
		}

		PageVo pageVo = PageVo.builder()
				.totalPage(reviews.getTotalPages())
				.nowPage(pageNumber)
				.totalElement(reviews.getTotalElements())
				.build();

		ReviewSelectionResponseResult reviewSelectionResponseResult = ReviewSelectionResponseResult.builder()
				.reviewSelectionResponses(reviewResponses)
				.pageVo(pageVo)
				.build();
		return reviewSelectionResponseResult;
	}

	public ReviewSelectionResponseResult getReviewListOrderByNewest(Long userId, Long productId, Integer pageNumber) {
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("user_circuitbreaker");

		List<ReviewSelectionResponse> reviewResponse = new ArrayList<>();
		PageRequest pageRequest = PageRequest.of(pageNumber, 8, Sort.by("reviewCreatedAt").descending());

		Page<Review> reviews = reviewPagingRepository.findReviewListByNewest(pageRequest, productId);

		for (Review review : reviews) {
			UserClientUserShortInfoResponse user
					= circuitBreaker.run(
					()->userServiceClient.findUserNameByUserId(review.getUserId()),
					throwable -> new UserClientUserShortInfoResponse());
			ReviewSelectionResponse reviewSelectionResponse = ReviewSelectionResponse
					.builder()
					.reviewId(review.getReviewId())
					.reviewContent(review.getReviewContent())
					.reviewCreatedAt(review.getReviewCreatedAt())
					.reviewModifiedAt(review.getReviewModifiedAt())
					.reviewLikeCount(review.getReviewLikeCount())
					.reviewRate(review.getReviewRate())
					.userName(user.getUserName())
					.userProfileImg(user.getUserProfileImg())
					.isMyReview(false)
					.isAvailableUp(true)
					.build();

			if(review.getUserId() == userId){
				reviewSelectionResponse.setIsMyReview(true);
			}
			Optional<ReviewLike> reviewLike = reviewLikeRepository.findReviewLikeByUser(userId, review.getReviewId());
			if(reviewLike.isPresent()){
				reviewSelectionResponse.setIsAvailableUp(false);
			}
			reviewResponse.add(reviewSelectionResponse);
		}
		PageVo pageVo = PageVo.builder()
				.totalPage(reviews.getTotalPages())
				.nowPage(pageNumber)
				.totalElement(reviews.getTotalElements())
				.build();
		ReviewSelectionResponseResult reviewSelectionResponseResult =ReviewSelectionResponseResult.builder()
				.reviewSelectionResponses(reviewResponse)
				.pageVo(pageVo)
				.build();

		return reviewSelectionResponseResult;
	}

	public ReviewSelectionResponseResult getReviewBySellerNewest(Long sellerId, Integer pageNumber) {
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("user_circuitbreaker");

		PageRequest pageRequest = PageRequest.of(pageNumber, 8, Sort.by("reviewCreatedAt").descending());
		Page<Review> reviews = reviewPagingRepository.findReviewListBySeller(pageRequest, sellerId);

		List<ReviewSelectionResponse> reviewResponse = new ArrayList<>();

		for (Review review : reviews) {
			UserClientUserShortInfoResponse user
					= circuitBreaker.run(
					()->userServiceClient.findUserNameByUserId(review.getUserId()),
					throwable -> new UserClientUserShortInfoResponse());
			ReviewSelectionResponse reviewSelectionResponse = ReviewSelectionResponse.builder()
					.reviewId(review.getReviewId())
					.reviewContent(review.getReviewContent())
					.reviewCreatedAt(review.getReviewCreatedAt())
					.reviewModifiedAt(review.getReviewModifiedAt())
					.reviewLikeCount(review.getReviewLikeCount())
					.reviewRate(review.getReviewRate())
					.userProfileImg(user.getUserProfileImg())
					.userEmail(user.getUserEmail())
					.productMainImgSrc(review.getProduct().getProductMainImgSrc())
					.productName(review.getProduct().getProductName())
					.userName(user.getUserName())
					.build();
			reviewResponse.add(reviewSelectionResponse);
		}
		PageVo pageVo = PageVo.builder()
				.totalPage(reviews.getTotalPages())
				.nowPage(pageNumber)
				.totalElement(reviews.getTotalElements())
				.build();
		ReviewSelectionResponseResult reviewSelectionResponseResult =ReviewSelectionResponseResult.builder()
				.reviewSelectionResponses(reviewResponse)
				.pageVo(pageVo)
				.build();

		return reviewSelectionResponseResult;
	}

	public ReviewSelectionResponseResult getMyReview(Long userId, Integer pageNumber) {
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("user_circuitbreaker");

		PageRequest pageRequest = PageRequest.of(pageNumber, 8, Sort.by("reviewCreatedAt").descending());
		Page<Review> reviews = reviewPagingRepository.findReviewListByUser(pageRequest, userId);

		List<ReviewSelectionResponse> reviewResponse = new ArrayList<>();

		for (Review review : reviews) {
			UserClientUserShortInfoResponse user
					= circuitBreaker.run(
					()->userServiceClient.findUserNameByUserId(review.getUserId()),
					throwable -> new UserClientUserShortInfoResponse());
			ReviewSelectionResponse reviewSelectionResponse = ReviewSelectionResponse.builder()
					.reviewId(review.getReviewId())
					.reviewContent(review.getReviewContent())
					.reviewCreatedAt(review.getReviewCreatedAt())
					.reviewModifiedAt(review.getReviewModifiedAt())
					.reviewLikeCount(review.getReviewLikeCount())
					.reviewRate(review.getReviewRate())
					.userProfileImg(user.getUserProfileImg())
					.userEmail(user.getUserEmail())
					.productMainImgSrc(review.getProduct().getProductMainImgSrc())
					.productName(review.getProduct().getProductName())
					.userName(user.getUserName())
					.isAvailableUp(true)
					.build();
			Optional<ReviewLike> reviewLike = reviewLikeRepository.findReviewLikeByUser(userId, review.getReviewId());
			if(reviewLike.isPresent()){
				reviewSelectionResponse.setIsAvailableUp(false);
			}
			reviewResponse.add(reviewSelectionResponse);
		}
		PageVo pageVo = PageVo.builder()
				.totalPage(reviews.getTotalPages())
				.nowPage(pageNumber)
				.totalElement(reviews.getTotalElements())
				.build();
		ReviewSelectionResponseResult reviewSelectionResponseResult =ReviewSelectionResponseResult.builder()
				.reviewSelectionResponses(reviewResponse)
				.pageVo(pageVo)
				.build();

		return reviewSelectionResponseResult;
	}
}
