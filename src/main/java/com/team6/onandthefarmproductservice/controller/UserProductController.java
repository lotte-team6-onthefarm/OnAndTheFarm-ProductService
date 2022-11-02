package com.team6.onandthefarmproductservice.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.team6.onandthefarmproductservice.dto.product.ProductWishCancelDto;
import com.team6.onandthefarmproductservice.dto.product.ProductWishFormDto;
import com.team6.onandthefarmproductservice.dto.product.ProductWishResultDto;
import com.team6.onandthefarmproductservice.service.ProductService;
import com.team6.onandthefarmproductservice.util.BaseResponse;
import com.team6.onandthefarmproductservice.vo.product.ProductDetailResponse;
import com.team6.onandthefarmproductservice.vo.product.ProductQnAInfoResponse;
import com.team6.onandthefarmproductservice.vo.product.ProductQnAResponse;
import com.team6.onandthefarmproductservice.vo.product.ProductSelectionResponse;
import com.team6.onandthefarmproductservice.vo.product.ProductSelectionResponseResult;
import com.team6.onandthefarmproductservice.vo.product.ProductWishCancelRequest;
import com.team6.onandthefarmproductservice.vo.product.ProductWishFormRequest;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/user/product")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserProductController {

	private final ProductService productService;

	@PostMapping(value = "/wish/add")
	@ApiOperation("위시리스트 추가")
	public ResponseEntity<BaseResponse> addProductToWishList(@ApiIgnore Principal principal,
			@RequestBody ProductWishFormRequest productWishFormRequest) throws Exception {

		if(principal == null){
			BaseResponse baseResponse = BaseResponse.builder()
					.httpStatus(HttpStatus.FORBIDDEN)
					.message("no authorization")
					.build();
			return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
		}

		String[] principalInfo = principal.getName().split(" ");
		Long userId = Long.parseLong(principalInfo[0]);

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		ProductWishFormDto productWishFormDto = modelMapper.map(productWishFormRequest, ProductWishFormDto.class);
		productWishFormDto.setUserId(userId);
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

		if(principal == null){
			BaseResponse baseResponse = BaseResponse.builder()
					.httpStatus(HttpStatus.FORBIDDEN)
					.message("no authorization")
					.build();
			return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
		}

		String[] principalInfo = principal.getName().split(" ");
		Long userId = Long.parseLong(principalInfo[0]);

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		ProductWishCancelDto productWishCancelDto = modelMapper.map(productWishCancelRequest,
				ProductWishCancelDto.class);
		productWishCancelDto.setUserId(userId);
		List<Long> wishId = productService.cancelProductFromWishList(productWishCancelDto);

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.CREATED)
				.message("delete Product to wish-list completed")
				.data(wishId)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/detail/{product-id}")
	@ApiOperation(value = "상품 단건 조회")
	public ResponseEntity<ProductDetailResponse> findProductDetail(@ApiIgnore Principal principal,
																   @PathVariable("product-id") Long productId,
																   @RequestParam Map<String, String> request) {

		Long userId = null;
		if (principal != null){
			String[] principalInfo = principal.getName().split(" ");
			userId = Long.parseLong(principalInfo[0]);
		}

		Long feedNumber = null;
		if(request.containsKey("number")){
			feedNumber = Long.parseLong(request.get("number"));
		}

		ProductDetailResponse product = productService.findProductDetail(productId, userId, feedNumber);

		BaseResponse baseReponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting one products by product-id")
				.data(product)
				.build();

		return new ResponseEntity(baseReponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/all/newest/{page-no}")
	@ApiOperation(value = "모든 상품 최신순 조회")
	public ResponseEntity<BaseResponse<ProductSelectionResponseResult>> getAllProductListOrderByNewest(
			@ApiIgnore Principal principal,
			@PathVariable("page-no") String pageNumber) {

		Long userId = null;
		if (principal != null){
			String[] principalInfo = principal.getName().split(" ");
			userId = Long.parseLong(principalInfo[0]);
		}
		ProductSelectionResponseResult products = productService.getAllProductListOrderByNewest(userId, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting All products by Newest")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/all/highprice/{page-no}")
	@ApiOperation(value = "상품 높은 가격 순 조회")
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getProductListByHighPrice(
			@ApiIgnore Principal principal,
			@PathVariable("page-no") String pageNumber) {

		Long userId = null;
		if (principal != null){
			String[] principalInfo = principal.getName().split(" ");
			userId = Long.parseLong(principalInfo[0]);
		}
		ProductSelectionResponseResult products = productService.getProductsListByHighPrice(userId,
				Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting products by high price completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/all/lowprice/{page-no}")
	@ApiOperation(value = "상품 낮은 가격 순 조회")
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getProductListByLowPrice(
			@ApiIgnore Principal principal,
			@PathVariable("page-no") String pageNumber) {

		Long userId = null;
		if (principal != null){
			String[] principalInfo = principal.getName().split(" ");
			userId = Long.parseLong(principalInfo[0]);
		}
		ProductSelectionResponseResult products = productService.getProductsListByLowPrice(userId, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting products by high price completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/main")
	@ApiOperation(value = "상품 메인 화면 조회(판매순 10개)")
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getMainProductsListBySoldCount(
			@ApiIgnore Principal principal){
		Long userId = null;
		if(principal != null){
			String [] principalInfo = principal.getName().split(" ");
			userId = Long.parseLong(principalInfo[0]);
		}
		ProductSelectionResponseResult products = productService.getMainProductsBySoldCount(userId);
		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting main view products by sold count")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/all/soldcount/{page-no}")
	@ApiOperation(value = "상품 높은 판매순 조회")
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getProductsListBySoldCount(
			@ApiIgnore Principal principal,
			@PathVariable("page-no") String pageNumber) {

		Long userId = null;
		if (principal != null){
			String[] principalInfo = principal.getName().split(" ");
			userId = Long.parseLong(principalInfo[0]);
		}
		ProductSelectionResponseResult products = productService.getProductsBySoldCount(userId, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting products by sold count completed")
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
			String[] principalInfo = principal.getName().split(" ");
			userId = Long.parseLong(principalInfo[0]);
		}
		ProductSelectionResponseResult products = productService.getProductListBySellerNewest(userId, sellerId,
				Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting Newest products by farmer completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/list/{category}/newest/{page-no}")
	@ApiOperation(value = "상품 카테고리별 최신순 조회")
	public ResponseEntity<BaseResponse<List<ProductSelectionResponse>>> getProductsListByCategoryNewest(
			@ApiIgnore Principal principal,
			@PathVariable("category") String category, @PathVariable("page-no") String pageNumber) {

		Long userId = null;
		if (principal != null){
			String[] principalInfo = principal.getName().split(" ");
			userId = Long.parseLong(principalInfo[0]);
		}
		ProductSelectionResponseResult products = productService.getProductListByCategoryNewest(userId, category,
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
		if (principal != null){
			String[] principalInfo = principal.getName().split(" ");
			userId = Long.parseLong(principalInfo[0]);
		}
		ProductSelectionResponseResult products = productService.getProductListByCategoryHighest(userId, category, Integer.valueOf(pageNumber));

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
		if (principal != null){
			String[] principalInfo = principal.getName().split(" ");
			userId = Long.parseLong(principalInfo[0]);
		}
		ProductSelectionResponseResult products = productService.getProductListByCategoryLowest(userId, category, Integer.valueOf(pageNumber));
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
		if (principal != null){
			String[] principalInfo = principal.getName().split(" ");
			userId = Long.parseLong(principalInfo[0]);
		}
		ProductSelectionResponseResult products = productService.getProductsByCategorySoldCount(userId, category, Integer.valueOf(pageNumber));

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("getting products by high price completed")
				.data(products)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}

	@GetMapping("/QnA/{product-no}")
	@ApiOperation(value = "상품에 대한 질의 조회")
	public ResponseEntity<BaseResponse<ProductQnAInfoResponse>> findProductQnAList(
			@PathVariable("product-no") Long productId) {

		List<ProductQnAResponse> products
				= productService.findProductQnAList(productId);

		ProductQnAInfoResponse productQnAInfoResponse = ProductQnAInfoResponse.builder()
				.productQnAResponseList(products)
				.qnACount(products.size())
				.build();

		BaseResponse baseResponse = BaseResponse.builder()
				.httpStatus(HttpStatus.OK)
				.message("OK")
				.data(productQnAInfoResponse)
				.build();

		return new ResponseEntity(baseResponse, HttpStatus.OK);
	}
}