package com.team6.onandthefarmproductservice.vo.review;

import java.util.List;

import com.team6.onandthefarmproductservice.vo.PageVo;

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
public class ReviewSelectionResponseResult {
	private List<ReviewSelectionResponse> reviewSelectionResponses;
	private PageVo pageVo;
}
