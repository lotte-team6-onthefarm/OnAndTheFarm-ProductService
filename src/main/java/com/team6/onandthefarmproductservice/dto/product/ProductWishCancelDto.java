package com.team6.onandthefarmproductservice.dto.product;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class
ProductWishCancelDto {
	private List<Long> wishId;
	private Long userId;
}
