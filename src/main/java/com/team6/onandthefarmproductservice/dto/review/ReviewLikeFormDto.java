package com.team6.onandthefarmproductservice.dto.review;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewLikeFormDto {
	private Long reviewId;
	private Long userId;
}
