package com.team6.onandthefarmproductservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team6.onandthefarmproductservice.dto.ReviewDeleteDto;
import com.team6.onandthefarmproductservice.dto.ReviewFormDto;
import com.team6.onandthefarmproductservice.dto.ReviewLikeCancelFormDto;
import com.team6.onandthefarmproductservice.dto.ReviewLikeFormDto;
import com.team6.onandthefarmproductservice.dto.ReviewUpdateFormDto;
import com.team6.onandthefarmproductservice.entity.Product;
import com.team6.onandthefarmproductservice.entity.Review;
import com.team6.onandthefarmproductservice.entity.ReviewLike;
import com.team6.onandthefarmproductservice.feignclient.OrderServiceClient;
import com.team6.onandthefarmproductservice.feignclient.SellerServiceClient;
import com.team6.onandthefarmproductservice.feignclient.UserServiceClient;
import com.team6.onandthefarmproductservice.repository.ProductRepository;
import com.team6.onandthefarmproductservice.repository.ReviewLikeRepository;
import com.team6.onandthefarmproductservice.repository.ReviewPagingRepository;
import com.team6.onandthefarmproductservice.repository.ReviewRepository;
import com.team6.onandthefarmproductservice.util.DateUtils;
import com.team6.onandthefarmproductservice.vo.OrderClientOrderProductIdResponse;
import com.team6.onandthefarmproductservice.vo.ReviewSelectionResponse;
import com.team6.onandthefarmproductservice.vo.UserClientUserShortInfo;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService{

	private final ReviewRepository reviewRepository;
	private final ReviewPagingRepository reviewPagingRepository;
	private final ReviewLikeRepository reviewLikeRepository;
	// private final UserRepository userRepository;
	private final UserServiceClient userServiceClient;
	private final ProductRepository productRepository;
	// private final OrderProductRepository orderProductRepository;
	private final OrderServiceClient orderServiceClient;

	private final DateUtils dateUtils;
	private final Environment env;

	@Autowired ReviewServiceImpl(ReviewRepository reviewRepository, ReviewPagingRepository reviewPagingRepository,
								 ReviewLikeRepository reviewLikeRepository,
								 //UserRepository userRepository,
								 UserServiceClient userServiceClient,
								 ProductRepository productRepository,
								 //OrderProductRepository orderProductRepository,
								 OrderServiceClient orderServiceClient,
								 DateUtils dateUtils, Environment env){
		this.reviewRepository = reviewRepository;
		this.reviewPagingRepository = reviewPagingRepository;
		this.reviewLikeRepository = reviewLikeRepository;
		//this.userRepository = userRepository;
		this.userServiceClient = userServiceClient;
		this.productRepository = productRepository;
		//this.orderProductRepository = orderProductRepository;
		this.orderServiceClient = orderServiceClient;
		this.dateUtils = dateUtils;
		this.env = env;
	}

	public Long saveReview(ReviewFormDto reviewFormDto){
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		Review review = modelMapper.map(reviewFormDto, Review.class);

		OrderClientOrderProductIdResponse orderClientOrderProductIdResponse = orderServiceClient.findProductIdByOrderProductId(reviewFormDto.getOrderProductId());
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


	public List<ReviewSelectionResponse> getReviewListByLikeCount(Long userId, Long productId, Integer pageNumber){
		// msa 고려하여 다시 설계할 것
		// Product product = productRepository.findById(productId).get();
		// List<Review> reviews = reviewRepository.findReviewsByProductOrderByReviewLikeCountDesc(product);
		List<ReviewSelectionResponse> reviewResponses = new ArrayList<>();
		PageRequest pageRequest = PageRequest.of(pageNumber, 8, Sort.by("reviewLikeCount").descending());
		List<Review> reviews = reviewPagingRepository.findReviewListByLikeCount(pageRequest, productId);
		for (Review review : reviews) {
			ReviewSelectionResponse reviewSelectionResponse = ReviewSelectionResponse
					.builder()
					.reviewId(review.getReviewId())
					.reviewContent(review.getReviewContent())
					.reviewCreatedAt(review.getReviewCreatedAt())
					.reviewModifiedAt(review.getReviewModifiedAt())
					.reviewLikeCount(review.getReviewLikeCount())
					.reviewRate(review.getReviewRate())
					.userName(userServiceClient.findUserNameByUserId(review.getUserId()).getUserName())
					.isMyReview(false)
					.isAvailableUp(false)
					.build();

			if(review.getUserId() == userId){
				reviewSelectionResponse.setIsMyReview(true);
			}
			Optional<ReviewLike> reviewLike = reviewLikeRepository.findReviewLikeByUser(userId, review.getReviewId());
			if(reviewLike.isPresent()){
				reviewSelectionResponse.setIsAvailableUp(false);
			}
			reviewResponses.add(reviewSelectionResponse);
		}
		return reviewResponses;
	}

	public List<ReviewSelectionResponse> getReviewListOrderByNewest(Long userId, Long productId, Integer pageNumber) {
		List<ReviewSelectionResponse> reviewResponse = new ArrayList<>();
		PageRequest pageRequest = PageRequest.of(pageNumber, 8, Sort.by("reviewCreatedAt").descending());
		List<Review> reviews = reviewPagingRepository.findReviewListByNewest(pageRequest, productId);
		for (Review review : reviews) {
			ReviewSelectionResponse reviewSelectionResponse = ReviewSelectionResponse
					.builder()
					.reviewId(review.getReviewId())
					.reviewContent(review.getReviewContent())
					.reviewCreatedAt(review.getReviewCreatedAt())
					.reviewModifiedAt(review.getReviewModifiedAt())
					.reviewLikeCount(review.getReviewLikeCount())
					.reviewRate(review.getReviewRate())
					.userName(userServiceClient.findUserNameByUserId(review.getUserId()).getUserName())
					.isMyReview(false)
					.isAvailableUp(false)
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
		return reviewResponse;
	}

	public List<ReviewSelectionResponse> getReviewBySellerNewest(Long sellerId, Integer pageNumber) {

		PageRequest pageRequest = PageRequest.of(pageNumber, 8, Sort.by("reviewCreatedAt").descending());
		List<Review> reviews = reviewPagingRepository.findReviewListBySeller(pageRequest, sellerId);

		List<ReviewSelectionResponse> reviewResponse = new ArrayList<>();

		for (Review review : reviews) {
			UserClientUserShortInfo userClientUserShortInfo = userServiceClient.findUserNameByUserId(review.getUserId());
			ReviewSelectionResponse reviewSelectionResponse = ReviewSelectionResponse.builder()
					.reviewId(review.getReviewId())
					.reviewContent(review.getReviewContent())
					.reviewCreatedAt(review.getReviewCreatedAt())
					.reviewModifiedAt(review.getReviewModifiedAt())
					.reviewLikeCount(review.getReviewLikeCount())
					.reviewRate(review.getReviewRate())
					.userProfileImg(userClientUserShortInfo.getUserProfileImg())
					.userEmail(userClientUserShortInfo.getUserEmail())
					.productMainImgSrc(review.getProduct().getProductMainImgSrc())
					.productName(review.getProduct().getProductName())
					.userName(userClientUserShortInfo.getUserName())
					.build();
			reviewResponse.add(reviewSelectionResponse);
		}

		return reviewResponse;
	}

	public List<ReviewSelectionResponse> getMyReview(Long userId, Integer pageNumber) {
		PageRequest pageRequest = PageRequest.of(pageNumber, 8, Sort.by("reviewCreatedAt").descending());
		List<Review> reviews = reviewPagingRepository.findReviewListByUser(pageRequest, userId);

		List<ReviewSelectionResponse> reviewResponse = new ArrayList<>();

		for (Review review : reviews) {
			UserClientUserShortInfo userClientUserShortInfo = userServiceClient.findUserNameByUserId(review.getUserId());
			ReviewSelectionResponse reviewSelectionResponse = ReviewSelectionResponse.builder()
					.reviewId(review.getReviewId())
					.reviewContent(review.getReviewContent())
					.reviewCreatedAt(review.getReviewCreatedAt())
					.reviewModifiedAt(review.getReviewModifiedAt())
					.reviewLikeCount(review.getReviewLikeCount())
					.reviewRate(review.getReviewRate())
					.userProfileImg(userClientUserShortInfo.getUserProfileImg())
					.userEmail(userClientUserShortInfo.getUserEmail())
					.productMainImgSrc(review.getProduct().getProductMainImgSrc())
					.productName(review.getProduct().getProductName())
					.userName(userClientUserShortInfo.getUserName())
					.build();
			reviewResponse.add(reviewSelectionResponse);
		}
		return reviewResponse;
	}
}
