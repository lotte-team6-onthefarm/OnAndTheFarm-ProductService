package com.team6.onandthefarmproductservice.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSelectionResponse {

	private Long reviewId;
	private String reviewContent;
	private String reviewCreatedAt;
	private String reviewModifiedAt;
	private Integer reviewLikeCount;
	private Integer reviewRate;
	private String userProfileImg;
	private String userEmail;
	private String productMainImgSrc;
	private String productName;
	private String userName;
	private Boolean isAvailableUp;
	private Boolean isMyReview;

}
