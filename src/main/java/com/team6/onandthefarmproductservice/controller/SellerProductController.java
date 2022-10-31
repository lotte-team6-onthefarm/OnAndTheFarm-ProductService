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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.team6.onandthefarmproductservice.dto.product.ProductDeleteDto;
import com.team6.onandthefarmproductservice.dto.product.ProductFormDto;
import com.team6.onandthefarmproductservice.dto.product.ProductUpdateFormDto;
import com.team6.onandthefarmproductservice.entity.Product;
import com.team6.onandthefarmproductservice.service.ProductService;
import com.team6.onandthefarmproductservice.util.BaseResponse;
import com.team6.onandthefarmproductservice.vo.product.ProductDeleteRequest;
import com.team6.onandthefarmproductservice.vo.product.ProductFormRequest;
import com.team6.onandthefarmproductservice.vo.product.ProductSelectionResponse;
import com.team6.onandthefarmproductservice.vo.product.ProductSelectionResponseResult;
import com.team6.onandthefarmproductservice.vo.product.ProductUpdateFormRequest;

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
	public ResponseEntity<BaseResponse<Product>> productForm(
			@ApiIgnore Principal principal,
			@RequestPart(value = "images", required = false) List<MultipartFile> photo,
			@RequestPart(value = "data", required = false) ProductFormRequest productFormRequest
			) throws Exception {

		if(principal == null){
			BaseResponse baseResponse = BaseResponse.builder()
					.httpStatus(HttpStatus.FORBIDDEN)
					.message("no authorization")
					.build();
			return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
		}

		String[] principalInfo = principal.getName().split(" ");
		Long sellerId = Long.parseLong(principalInfo[0]);

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		ProductFormDto productFormDto = modelMapper.map(productFormRequest, ProductFormDto.class);
		productFormDto.setSellerId(sellerId);
		productFormDto.setImages(photo);

		Long productId = productService.saveProduct(productFormDto);

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
			@ApiIgnore Principal principal,
			@RequestPart(value = "images", required = false) List<MultipartFile> photo,
			@RequestPart(value = "mainImage", required = false) List<MultipartFile> mainPhoto,
			@RequestPart(value = "data", required = false) ProductUpdateFormRequest productUpdateFormRequest )
			throws Exception {

		if(principal == null){
			BaseResponse baseResponse = BaseResponse.builder()
					.httpStatus(HttpStatus.FORBIDDEN)
					.message("no authorization")
					.build();
			return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
		}

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		ProductUpdateFormDto productUpdateFormDto = modelMapper.map(productUpdateFormRequest,
				ProductUpdateFormDto.class);
		productUpdateFormDto.setAddImageList(photo);
		productUpdateFormDto.setMainImage(mainPhoto);

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
	public ResponseEntity<BaseResponse<ProductSelectionResponseResult>> getAllProductListOrderByNewest(
			@PathVariable("page-no") String pageNumber) {
		Long userId = null;
		ProductSelectionResponseResult products = productService.getAllProductListOrderByNewest(userId, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting All products by Newest")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/orderby/highprice/{page-no}")
	@ApiOperation(value = "상품 높은 가격 순 조회")
	public ResponseEntity<BaseResponse<ProductSelectionResponseResult>> getProductListByHighPrice(
			@PathVariable("page-no") String pageNumber) {

		Long userId = null;
		ProductSelectionResponseResult products = productService.getProductsListByHighPrice(userId, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting products by high price completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/orderby/lowprice/{page-no}")
	@ApiOperation(value = "상품 낮은 가격 순 조회")
	public ResponseEntity<BaseResponse<ProductSelectionResponseResult>> getProductListByLowPrice(
			@PathVariable("page-no") String pageNumber) {

		Long userId = null;
		ProductSelectionResponseResult products = productService.getProductsListByLowPrice(userId, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting products by high price completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/orderby/soldcount/{page-no}")
	@ApiOperation(value = "상품 높은 판매순 조회")
	public ResponseEntity<BaseResponse<ProductSelectionResponseResult>> getProductsListBySoldCount(
			@PathVariable("page-no") String pageNumber) {

		Long userId = null;
		ProductSelectionResponseResult products = productService.getProductsBySoldCount(userId, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting products by high price completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/orderby/seller/{sellerId}/{page-no}")
	@ApiOperation(value = "상품 농부별 최신순 조회")
	public ResponseEntity<BaseResponse<ProductSelectionResponseResult>> getProductsListBySellerNewest(
			@PathVariable("sellerId") Long sellerId, @PathVariable("page-no") String pageNumber) {

		Long userId = null;
		ProductSelectionResponseResult products = productService.getProductListBySellerNewest(userId, sellerId, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting Newest products by farmer completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/orderby/{category}/newest/{page-no}")
	@ApiOperation(value = "상품 카테고리별 최신순 조회")
	public ResponseEntity<BaseResponse<ProductSelectionResponseResult>> getProductsListByCategoryNewest(
			@PathVariable("category") String category, @PathVariable("page-no") String pageNumber) {

		Long userId = null;
		ProductSelectionResponseResult products = productService.getProductListByCategoryNewest(userId, category, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting Newest products by category  completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/selling-product/by-seller/{page-no}")
	@ApiOperation(value = "농부별 자신이 등록한 판매중 상품 최신순 조회")
	public ResponseEntity<BaseResponse<ProductSelectionResponseResult>> getSellingProductListBy(@ApiIgnore Principal principal, @PathVariable("page-no") String pageNumber) {

		if(principal == null){
			BaseResponse baseResponse = BaseResponse.builder()
					.httpStatus(HttpStatus.FORBIDDEN)
					.message("no authorization")
					.build();
			return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
		}

		String[] principalInfo = principal.getName().split(" ");
		Long sellerId = Long.parseLong(principalInfo[0]);
		Long userId = null;

		ProductSelectionResponseResult products = productService.getSellingProductListBySellerNewest(userId, sellerId, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting Newest products by farmer completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/pause-product/by-seller/{page-no}")
	@ApiOperation(value = "농부별 자신이 등록한 일시정지 상품 최신순 조회")
	public ResponseEntity<BaseResponse<ProductSelectionResponseResult>> getPauseProductListBy(@ApiIgnore Principal principal, @PathVariable("page-no") String pageNumber) {

		if(principal == null){
			BaseResponse baseResponse = BaseResponse.builder()
					.httpStatus(HttpStatus.FORBIDDEN)
					.message("no authorization")
					.build();
			return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
		}

		String[] principalInfo = principal.getName().split(" ");
		Long sellerId = Long.parseLong(principalInfo[0]);
		Long userId = null;

		ProductSelectionResponseResult products = productService.getPauseProductListBySellerNewest(userId, sellerId, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting Newest products by farmer completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}
}