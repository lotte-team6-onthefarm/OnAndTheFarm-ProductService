package com.team6.onandthefarmproductservice.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team6.onandthefarmproductservice.service.ReviewService;
import com.team6.onandthefarmproductservice.util.BaseResponse;
import com.team6.onandthefarmproductservice.vo.review.ReviewSelectionResponse;
import com.team6.onandthefarmproductservice.vo.review.ReviewSelectionResponseResult;

import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/seller/review")
@RequiredArgsConstructor
public class SellerReviewController {
	private final ReviewService reviewService;

	@GetMapping("/list/by-seller/{page-no}")
	public ResponseEntity<BaseResponse<ReviewSelectionResponseResult>> getReviewBySellerNewest(@ApiIgnore Principal principal, @PathVariable("page-no") String pageNumber){

		if(principal == null){
			BaseResponse baseResponse = BaseResponse.builder()
					.httpStatus(HttpStatus.FORBIDDEN)
					.message("no authorization")
					.build();
			return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
		}

		String[] principalInfo = principal.getName().split(" ");
		Long sellerId = Long.parseLong(principalInfo[0]);

		ReviewSelectionResponseResult reviews = reviewService.getReviewBySellerNewest(sellerId, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("get reviews by seller completed")
				.data(reviews)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}
}
