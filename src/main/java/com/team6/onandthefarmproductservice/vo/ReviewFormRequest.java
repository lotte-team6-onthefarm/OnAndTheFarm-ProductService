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
public class ReviewFormRequest {
	private Long orderProductId;
	private String reviewContent;
	private String reviewCreatedAt;
	private String reviewModifiedAt;
	private Integer reviewRate;
}
