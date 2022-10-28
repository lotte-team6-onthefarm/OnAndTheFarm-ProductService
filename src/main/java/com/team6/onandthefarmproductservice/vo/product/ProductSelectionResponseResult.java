package com.team6.onandthefarmproductservice.vo.product;

import java.util.List;

import org.springframework.data.domain.Page;

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
public class ProductSelectionResponseResult {
	private List<ProductSelectionResponse> productSelectionResponses;
	private PageVo pageVo;
}
