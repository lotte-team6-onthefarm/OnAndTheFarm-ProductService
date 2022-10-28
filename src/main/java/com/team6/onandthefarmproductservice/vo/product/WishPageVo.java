package com.team6.onandthefarmproductservice.vo.product;

import org.springframework.data.domain.PageRequest;

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
public class WishPageVo {
	private Integer pageNumber;
	private PageRequest pageRequest;
}
