package com.team6.onandthefarmproductservice.feignclient.vo;

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
public class ReviewInfoToExbt {
	private double reviewRate;
	private Integer reviewCount;
}
