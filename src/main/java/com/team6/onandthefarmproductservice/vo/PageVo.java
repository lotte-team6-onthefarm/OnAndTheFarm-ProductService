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
public class PageVo {
	private Integer totalPage;
	private Integer nowPage;
	private Long totalElement;
}
