package com.team6.onandthefarmproductservice.controller;

import java.security.Principal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team6.onandthefarmproductservice.dto.review.ReviewDeleteDto;
import com.team6.onandthefarmproductservice.dto.review.ReviewFormDto;
import com.team6.onandthefarmproductservice.dto.review.ReviewLikeCancelFormDto;
import com.team6.onandthefarmproductservice.dto.review.ReviewLikeFormDto;
import com.team6.onandthefarmproductservice.dto.review.ReviewUpdateFormDto;
import com.team6.onandthefarmproductservice.entity.Review;
import com.team6.onandthefarmproductservice.entity.ReviewLike;
import com.team6.onandthefarmproductservice.service.ReviewService;
import com.team6.onandthefarmproductservice.util.BaseResponse;
import com.team6.onandthefarmproductservice.vo.review.ReviewDeleteRequest;
import com.team6.onandthefarmproductservice.vo.review.ReviewFormRequest;
import com.team6.onandthefarmproductservice.vo.review.ReviewInfoResponse;
import com.team6.onandthefarmproductservice.vo.review.ReviewLikeCancelFormRequest;
import com.team6.onandthefarmproductservice.vo.review.ReviewLikeFormRequest;
import com.team6.onandthefarmproductservice.vo.review.ReviewSelectionResponse;
import com.team6.onandthefarmproductservice.vo.review.ReviewSelectionResponseResult;
import com.team6.onandthefarmproductservice.vo.review.ReviewUpdateFormRequest;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/user/review")
@RequiredArgsConstructor
public class UserReviewController {
	private final ReviewService reviewService;

	@PostMapping("/new")
	@ApiOperation("리뷰 신규 등록")
	public ResponseEntity<BaseResponse<Review>> reviewForm(@ApiIgnore Principal principal,
														   @RequestBody ReviewFormRequest reviewFormRequest) throws Exception{

		if(principal == null){
			BaseResponse baseResponse = BaseResponse.builder()
					.httpStatus(HttpStatus.FORBIDDEN)
					.message("no authorization")
					.build();
			return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
		}

		String[] principalInfo = principal.getName().split(" ");
		Long userId = Long.parseLong(principalInfo[0]);

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		ReviewFormDto reviewFormDto = modelMapper.map(reviewFormRequest, ReviewFormDto.class);
		reviewFormDto.setUserId(userId);

		Long reviewId = reviewService.saveReview(reviewFormDto);

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.CREATED)
				.message("review register completed")
				.data(reviewId)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.CREATED);
	}

	@PutMapping(value="/update")
	@ApiOperation("리뷰 수정")
	public ResponseEntity<BaseResponse<Review>> reviewUpdateForm(@RequestBody ReviewUpdateFormRequest reviewUpdateFormRequest) throws Exception{

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		ReviewUpdateFormDto reviewUpdateFormDto = modelMapper.map(reviewUpdateFormRequest, ReviewUpdateFormDto.class);
		Long reviewId = reviewService.updateReview(reviewUpdateFormDto);

		if(reviewId.equals(0l)){
			BaseResponse baseResponse = BaseResponse.builder()
					.httpStatus(HttpStatus.BAD_REQUEST)
					.message("review update completed")
					.data(reviewId)
					.build();
			return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
		}
		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("review update completed")
				.data(reviewId)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@PutMapping(value="/delete")
	@ApiOperation("리뷰 삭제")
	public ResponseEntity<BaseResponse<Review>> productDelete(@RequestBody ReviewDeleteRequest reviewDeleteRequest) throws Exception{
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		ReviewDeleteDto reviewDeleteDto = modelMapper.map(reviewDeleteRequest, ReviewDeleteDto.class);
		Long reviewId = reviewService.deleteReview(reviewDeleteDto);

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("review delete completed")
				.data(reviewId)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	// review like
	@PostMapping(value="/like/up")
	@ApiOperation("리뷰 좋아요 +1")
	public ResponseEntity<BaseResponse<ReviewLike>> upReviewLikeCount(@ApiIgnore Principal principal,
																	  @RequestBody ReviewLikeFormRequest reviewLikeFormRequest) throws Exception{

		if(principal == null){
			BaseResponse baseResponse = BaseResponse.builder()
					.httpStatus(HttpStatus.FORBIDDEN)
					.message("no authorization")
					.build();
			return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
		}

		String[] principalInfo = principal.getName().split(" ");
		Long userId = Long.parseLong(principalInfo[0]);

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		ReviewLikeFormDto reviewLikeFormDto = modelMapper.map(reviewLikeFormRequest, ReviewLikeFormDto.class);
		reviewLikeFormDto.setUserId(userId);
		Long reviewLikeId = reviewService.upLikeCountReview(reviewLikeFormDto);

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.CREATED)
				.message("up reviewLike Counts completed")
				.data(reviewLikeId)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	//review like cancel
	@PutMapping(value="/like/cancel")
	@ApiOperation("리뷰 좋아요 취소 -1")
	public ResponseEntity<BaseResponse<ReviewLike>> cancelReviewLikeCount(@ApiIgnore Principal principal,
																		  @RequestBody ReviewLikeCancelFormRequest reviewLikeCancelFormRequest) throws Exception{

		if(principal == null){
			BaseResponse baseResponse = BaseResponse.builder()
					.httpStatus(HttpStatus.FORBIDDEN)
					.message("no authorization")
					.build();
			return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
		}

		String[] principalInfo = principal.getName().split(" ");
		Long userId = Long.parseLong(principalInfo[0]);

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		ReviewLikeCancelFormDto reviewLikeCancelFormDto = modelMapper.map(reviewLikeCancelFormRequest, ReviewLikeCancelFormDto.class);
		reviewLikeCancelFormDto.setUserId(userId);
		Long reviewLikeId = reviewService.cancelReviewLikeCount(reviewLikeCancelFormDto);

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("Cancel reviewLike counts completed")
				.data(reviewLikeId)
				.build();
		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping("/info")
	@ApiOperation("상품별로 리뷰에 관한 정보 조회")
	public ResponseEntity<BaseResponse<ReviewInfoResponse>> getReviewInfo(@RequestParam Long productId){

		ReviewInfoResponse reviewInfoResponse = reviewService.getReviewInfo(productId);

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("get review info completed")
				.data(reviewInfoResponse)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping("/list/orderby/likecount/{productId}/{page-no}")
	@ApiOperation("리뷰 좋아요순 조회")
	public ResponseEntity<BaseResponse<ReviewSelectionResponseResult>> getReviewListByLikeCount(@ApiIgnore Principal principal,
																								@PathVariable("productId") Long productId,
																								@PathVariable("page-no") String pageNumber){

		Long userId = 0l;
		if(principal != null) {
			String[] principalInfo = principal.getName().split(" ");
			userId = Long.parseLong(principalInfo[0]);
		}
		ReviewSelectionResponseResult reviews = reviewService.getReviewListByLikeCount(userId, productId, Integer.valueOf(pageNumber));

		//필요 부분만 보내기 위해 (셀러, 프로덕트 짜르기)
		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("get reviews order by likecount completed")
				.data(reviews)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}


	@GetMapping("/list/orderby/newest/{productId}/{page-no}")
	@ApiOperation("상품상세 리뷰조회 최신순")
	public ResponseEntity<BaseResponse<ReviewSelectionResponseResult>> getReviewOrderByNewest(@ApiIgnore Principal principal,
																							  @PathVariable("productId") Long productId,
																							  @PathVariable("page-no") String pageNumber){

		Long userId = null;
		if(principal != null) {
			String[] principalInfo = principal.getName().split(" ");
			userId = Long.parseLong(principalInfo[0]);
		}

		ReviewSelectionResponseResult reviews = reviewService.getReviewListOrderByNewest(userId, productId, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("get reviews order by registerdate completed")
				.data(reviews)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping("/list/my-review/{page-no}")
	@ApiOperation("내가쓴 리스트 리뷰 조회")
	public ResponseEntity<BaseResponse<ReviewSelectionResponseResult>> getMyReview(@ApiIgnore Principal principal, @PathVariable("page-no") String pageNumber){

		if(principal == null){
			BaseResponse baseResponse = BaseResponse.builder()
					.httpStatus(HttpStatus.FORBIDDEN)
					.message("no authorization")
					.build();
			return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
		}

		String[] principalInfo = principal.getName().split(" ");
		Long userId = Long.parseLong(principalInfo[0]);

		ReviewSelectionResponseResult reviews = reviewService.getMyReview(userId, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("get reviews List by User completed")
				.data(reviews)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}
}
