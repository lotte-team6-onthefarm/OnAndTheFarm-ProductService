package com.team6.onandthefarmproductservice.controller;

import java.security.Principal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team6.onandthefarmproductservice.dto.ProductDeleteDto;
import com.team6.onandthefarmproductservice.dto.ProductFormDto;
import com.team6.onandthefarmproductservice.dto.ProductUpdateFormDto;
import com.team6.onandthefarmproductservice.entity.Product;
import com.team6.onandthefarmproductservice.service.ProductService;
import com.team6.onandthefarmproductservice.util.BaseResponse;
import com.team6.onandthefarmproductservice.vo.ProductDeleteRequest;
import com.team6.onandthefarmproductservice.vo.ProductFormRequest;
import com.team6.onandthefarmproductservice.vo.ProductSelectionResponse;
import com.team6.onandthefarmproductservice.vo.ProductUpdateFormRequest;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/seller/product")
@RequiredArgsConstructor
public class SellerProductController {

	private final ProductService productService;

	@PostMapping(value = "/new")
	@ApiOperation(value = "상품 등록")
	//@RequestPart("productImg") List<MultipartFile> productImgs
	public ResponseEntity<BaseResponse<Product>> productForm(@ApiIgnore Principal principal, @RequestBody ProductFormRequest productFormRequest) throws
			Exception {

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		ProductFormDto productFormDto = modelMapper.map(productFormRequest, ProductFormDto.class);
		productFormDto.setSellerId(Long.parseLong(principal.getName()));
		Long sellerId = Long.parseLong(principal.getName());
		Long productId = productService.saveProduct(sellerId, productFormDto);

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.CREATED)
				.message("product register completed")
				.data(productId)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.CREATED);
	}

	@PutMapping(value = "/update")
	@ApiOperation(value = "상품 수정")
	public ResponseEntity<BaseResponse<Product>> productUpdateForm(
			@RequestBody ProductUpdateFormRequest productUpdateFormRequest) throws Exception {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		ProductUpdateFormDto productUpdateFormDto = modelMapper.map(productUpdateFormRequest,
				ProductUpdateFormDto.class);

		Long productId = productService.updateProduct(productUpdateFormDto);

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("product update completed")
				.data(productId)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@PutMapping(value = "/delete")
	@ApiOperation(value = "상품 삭제")
	public ResponseEntity<BaseResponse<Product>> productDelete(
			@RequestBody ProductDeleteRequest productDeleteRequest) throws Exception {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		ProductDeleteDto productDeleteDto = modelMapper.map(productDeleteRequest, ProductDeleteDto.class);

		Long productId = productService.deleteProduct(productDeleteDto);

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("product delete completed")
				.data(productId)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}


	@GetMapping(value = "/list/all/newest/{page-no}")
	@ApiOperation(value = "모든 상품 최신순 조회")
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getAllProductListOrderByNewest(@ApiIgnore Principal principal,
			@PathVariable("page-no") String pageNumber) {
		Long userId = null;
		Long sellerId = Long.parseLong(principal.getName());
		List<ProductSelectionResponse> products = productService.getAllProductListOrderByNewest(userId, sellerId, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting All products by Newest")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/orderby/highprice/{page-no}")
	@ApiOperation(value = "상품 높은 가격 순 조회")
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getProductListByHighPrice(@ApiIgnore Principal principal,
			@PathVariable("page-no") String pageNumber) {

		Long userId = null;
		Long sellerId = Long.parseLong(principal.getName());
		List<ProductSelectionResponse> products = productService.getProductsListByHighPrice(userId, sellerId, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting products by high price completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/orderby/lowprice/{page-no}")
	@ApiOperation(value = "상품 낮은 가격 순 조회")
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getProductListByLowPrice(@ApiIgnore Principal principal,
			@PathVariable("page-no") String pageNumber) {

		Long userId = null;
		Long sellerId = Long.parseLong(principal.getName());
		List<ProductSelectionResponse> products = productService.getProductsListByLowPrice(userId, sellerId, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting products by high price completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/orderby/soldcount/{page-no}")
	@ApiOperation(value = "상품 높은 판매순 조회")
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getProductsListBySoldCount(@ApiIgnore Principal principal,
			@PathVariable("page-no") String pageNumber) {

		Long userId = null;
		Long sellerId = Long.parseLong(principal.getName());
		List<ProductSelectionResponse> products = productService.getProductsBySoldCount(userId, sellerId, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting products by high price completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/orderby/seller/{sellerId}/{page-no}")
	@ApiOperation(value = "상품 농부별 최신순 조회")
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getProductsListBySellerNewest(@ApiIgnore Principal principal,
			@PathVariable("page-no") String pageNumber) {

		Long userId = null;
		Long sellerId = Long.parseLong(principal.getName());
		List<ProductSelectionResponse> products = productService.getProductListBySellerNewest(userId, sellerId, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting Newest products by farmer completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/orderby/{category}/{page-no}")
	@ApiOperation(value = "상품 카테고리별 최신순 조회")
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getProductsListByCategoryNewest(
			@PathVariable("category") String category, @PathVariable("page-no") String pageNumber) {

		Long userId = null;
		Long sellerId = null;
		List<ProductSelectionResponse> products = productService.getProductListByCategoryNewest(userId, sellerId, category, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting Newest products by category  completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/orderby/seller/{page-no}")
	@ApiOperation(value = "농부별 자신이 등록한 상품 최신순 조회")
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getNewestProductList(@ApiIgnore Principal principal, @PathVariable("page-no") String pageNumber) {

		Long userId = null;
		Long sellerId = Long.parseLong(principal.getName());
		List<ProductSelectionResponse> products = productService.getProductListBySellerNewest(userId, sellerId, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting Newest products by farmer completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	//셀러 기준 질의 조회 구현 요망!

}