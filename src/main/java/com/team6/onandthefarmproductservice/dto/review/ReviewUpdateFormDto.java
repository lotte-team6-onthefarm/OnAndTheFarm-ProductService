package com.team6.onandthefarmproductservice.dto.review;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewUpdateFormDto {
	private Long reviewId;
	private Long productId;
	private Long userId;
	@NotBlank(message = "리뷰 작성 글은 필수 입력 값입니다.")
	private String reviewContent;
	private String reviewCreatedAt;
	private String reviewModifiedAt;
	private Integer reviewLikeCount;
	private Integer reviewRate;
	private String reviewStatus;
}
