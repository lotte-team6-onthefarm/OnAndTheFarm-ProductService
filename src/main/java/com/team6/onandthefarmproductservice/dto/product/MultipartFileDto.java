package com.team6.onandthefarmproductservice.dto.product;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;

@Builder
public class MultipartFileDto {
	private MultipartFile productMultipartFile;
	private Long productId;
	private String productImgSrc;
}
