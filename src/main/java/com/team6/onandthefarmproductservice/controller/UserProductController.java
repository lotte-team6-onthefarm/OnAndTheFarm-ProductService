package com.team6.onandthefarmproductservice.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

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

import com.team6.onandthefarmproductservice.dto.ProductWishCancelDto;
import com.team6.onandthefarmproductservice.dto.ProductWishFormDto;
import com.team6.onandthefarmproductservice.dto.ProductWishResultDto;
import com.team6.onandthefarmproductservice.entity.ProductQna;
import com.team6.onandthefarmproductservice.entity.ProductQnaAnswer;
import com.team6.onandthefarmproductservice.service.ProductService;
import com.team6.onandthefarmproductservice.util.BaseResponse;
import com.team6.onandthefarmproductservice.vo.ProductDetailResponse;
import com.team6.onandthefarmproductservice.vo.ProductInfoResponse;
import com.team6.onandthefarmproductservice.vo.ProductQnAResponse;
import com.team6.onandthefarmproductservice.vo.ProductSelectionResponse;
import com.team6.onandthefarmproductservice.vo.ProductWishCancelRequest;
import com.team6.onandthefarmproductservice.vo.ProductWishFormRequest;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/user/product")
@RequiredArgsConstructor
public class UserProductController {

	private final ProductService productService;

	@PostMapping(value = "/wish/add")
	@ApiOperation("위시리스트 추가")
	public ResponseEntity<BaseResponse> addProductToWishList(@ApiIgnore Principal principal,
			@RequestBody ProductWishFormRequest productWishFormRequest) throws Exception {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		ProductWishFormDto productWishFormDto = modelMapper.map(productWishFormRequest, ProductWishFormDto.class);
		productWishFormDto.setUserId(Long.parseLong(principal.getName()));
		ProductWishResultDto resultDto = productService.addProductToWishList(productWishFormDto);

		BaseResponse baseResponse = null;
		if(resultDto.getIsCreated()) {
			baseResponse = BaseResponse.builder()
					.httpStatus(HttpStatus.CREATED)
					.message("add Product to wish-list completed")
					.data(resultDto.getWishId())
					.build();
		}
		else {
			baseResponse = BaseResponse.builder()
					.httpStatus(HttpStatus.OK)
					.message("Wish is already added")
					.data(resultDto.getWishId())
					.build();
		}

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@PutMapping(value = "/wish/delete")
	@ApiOperation("위시리스트 삭제")
	public ResponseEntity<BaseResponse> deleteProductToWishList(@ApiIgnore Principal principal,
			@RequestBody ProductWishCancelRequest productWishCancelRequest) throws Exception {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		ProductWishCancelDto productWishCancelDto = modelMapper.map(productWishCancelRequest,
				ProductWishCancelDto.class);
		productWishCancelDto.setUserId(Long.parseLong(principal.getName()));
		List<Long> wishId = productService.cancelProductFromWishList(productWishCancelDto);

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.CREATED)
				.message("delete Product to wish-list completed")
				.data(wishId)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/{product-id}")
	@ApiOperation(value = "상품 단건 조회")
	public ResponseEntity<ProductInfoResponse> findProductDetail(@ApiIgnore Principal principal, @PathVariable("product-id") Long productId) {
		Long userId = null;
		if (principal != null){
			userId = Long.parseLong(principal.getName());
		}
		ProductDetailResponse product = productService.findProductDetail(productId, userId);

		BaseResponse baseReponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting one products by product-id")
				.data(product)
				.build();

		return new ResponseEntity(baseReponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/all/newest/{page-no}")
	@ApiOperation(value = "모든 상품 최신순 조회")
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getAllProductListOrderByNewest(
			@ApiIgnore Principal principal,
			@PathVariable("page-no") String pageNumber) {

		Long userId = null;
		Long sellerId = null;
		if (principal != null){
			userId = Long.parseLong(principal.getName());
		}
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
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getProductListByHighPrice(
			@ApiIgnore Principal principal,
			@PathVariable("page-no") String pageNumber) {

		Long userId = null;
		Long sellerId = null;
		if (principal != null){
			userId = Long.parseLong(principal.getName());
		}
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
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getProductListByLowPrice(
			@ApiIgnore Principal principal,
			@PathVariable("page-no") String pageNumber) {

		Long userId = null;
		Long sellerId = null;
		if (principal != null){
			userId = Long.parseLong(principal.getName());
		}
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
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getProductsListBySoldCount(
			@ApiIgnore Principal principal,
			@PathVariable("page-no") String pageNumber) {

		Long userId = null;
		Long sellerId = null;
		if (principal != null){
			userId = Long.parseLong(principal.getName());
		}
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
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getProductsListBySellerNewest(
			@ApiIgnore Principal principal,
			@PathVariable("sellerId") Long sellerId, @PathVariable("page-no") String pageNumber) {

		Long userId = null;
		if (principal != null){
			userId = Long.parseLong(principal.getName());
		}
		List<ProductSelectionResponse> products = productService.getProductListBySellerNewest(userId, sellerId,
				Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting Newest products by farmer completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/{category}/{page-no}")
	@ApiOperation(value = "상품 카테고리별 최신순 조회")
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getProductsListByCategoryNewest(
			@ApiIgnore Principal principal,
			@PathVariable("category") String category, @PathVariable("page-no") String pageNumber) {

		Long userId = null;
		Long sellerId = null;
		if (principal != null){
			userId = Long.parseLong(principal.getName());
		}
		List<ProductSelectionResponse> products = productService.getProductListByCategoryNewest(userId, sellerId, category,
				Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting Newest products by category  completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/{category}/highprice/{page-no}")
	@ApiOperation(value = "상품 카테고리별 높은가격 순 조회")
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getProductListByCategoryHighest(
			@ApiIgnore Principal principal,
			@PathVariable("category") String category, @PathVariable("page-no") String pageNumber){
		Long userId = null;
		Long sellerId = null;
		if (principal != null){
			userId = Long.parseLong(principal.getName());
		}
		List<ProductSelectionResponse> products = productService.getProductListByCategoryHighest(userId, sellerId, category, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting highest products by category  completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/{category}/lowprice/{page-no}")
	@ApiOperation(value = "상품 카테고리별 낮은가격 순 조회")
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getProductListByCategoryLowest(
			@ApiIgnore Principal principal,
			@PathVariable("category") String category, @PathVariable("page-no") String pageNumber){
		Long userId = null;
		Long sellerId = null;
		if (principal != null){
			userId = Long.parseLong(principal.getName());
		}
		List<ProductSelectionResponse> products = productService.getProductListByCategoryLowest(userId, sellerId, category, Integer.valueOf(pageNumber));
		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting lowest products by category completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/{category}/soldcount/{page-no}")
	@ApiOperation(value = "상품 카테고리별 판매 순 조회")
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getProductsListByCategorySoldCount(
			@ApiIgnore Principal principal,
			@PathVariable("category") String category, @PathVariable("page-no") String pageNumber) {

		Long userId = null;
		Long sellerId = null;
		if (principal != null){
			userId = Long.parseLong(principal.getName());
		}
		List<ProductSelectionResponse> products = productService.getProductsByCategorySoldCount(userId, sellerId, category, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting products by high price completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping("/QnA/{product-no}")
	@ApiOperation(value = "상품에 대한 질의 조회")
	public ResponseEntity<BaseResponse<Map<ProductQna, ProductQnaAnswer>>> findProductQnAList(
			@PathVariable("product-no") Long productId) {

		List<ProductQnAResponse> products
				= productService.findProductQnAList(productId);

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("OK")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}
}