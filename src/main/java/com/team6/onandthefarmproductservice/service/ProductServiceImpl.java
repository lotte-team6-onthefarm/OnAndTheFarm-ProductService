package com.team6.onandthefarmproductservice.service;

import java.io.IOException;
import java.util.*;

import com.team6.onandthefarmproductservice.dto.product.*;
import com.team6.onandthefarmproductservice.entity.*;
import com.team6.onandthefarmproductservice.vo.PageVo;
import com.team6.onandthefarmproductservice.vo.order.OrderClientSellerIdAndDateResponse;
import com.team6.onandthefarmproductservice.vo.product.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.team6.onandthefarmproductservice.feignclient.OrderServiceClient;
import com.team6.onandthefarmproductservice.feignclient.SellerServiceClient;
import com.team6.onandthefarmproductservice.feignclient.UserServiceClient;
import com.team6.onandthefarmproductservice.repository.CartRepository;
import com.team6.onandthefarmproductservice.repository.CategoryRepository;
import com.team6.onandthefarmproductservice.repository.ProductImgRepository;
import com.team6.onandthefarmproductservice.repository.ProductPagingRepository;
import com.team6.onandthefarmproductservice.repository.ProductQnaAnswerRepository;
import com.team6.onandthefarmproductservice.repository.ProductQnaRepository;
import com.team6.onandthefarmproductservice.repository.ProductRepository;
import com.team6.onandthefarmproductservice.repository.ProductWishRepository;
import com.team6.onandthefarmproductservice.repository.ReviewRepository;
import com.team6.onandthefarmproductservice.util.DateUtils;
import com.team6.onandthefarmproductservice.util.S3Upload;
import com.team6.onandthefarmproductservice.vo.order.OrderClientOrderProductIdResponse;
import com.team6.onandthefarmproductservice.vo.order.OrdersByUserResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j

public class ProductServiceImpl implements ProductService {

	private final int listNum = 5;
	private final int pageContentNumber = 8;
	private ProductRepository productRepository;
	private CategoryRepository categoryRepository;
	private ProductQnaRepository productQnaRepository;
	private ProductQnaAnswerRepository productQnaAnswerRepository;
	private SellerServiceClient sellerServiceClient;
	private UserServiceClient userServiceClient;
	private OrderServiceClient orderServiceClient;
	private ProductPagingRepository productPagingRepository;
	private ProductImgRepository productImgRepository;
	private ProductWishRepository productWishRepository;
	private CartRepository cartRepository;
	private ReviewRepository reviewRepository;
	private DateUtils dateUtils;

	private S3Upload s3Upload;
	private Environment env;

	@Autowired
	public ProductServiceImpl(ProductRepository productRepository,
							  CategoryRepository categoryRepository,
							  DateUtils dateUtils,
							  Environment env,
							  ProductQnaRepository productQnaRepository,
							  ProductQnaAnswerRepository productQnaAnswerRepository,
							  SellerServiceClient sellerServiceClient,
							  UserServiceClient userServiceClient,
							  OrderServiceClient orderServiceClient,
							  ProductWishRepository productWishRepository,
							  CartRepository cartRepository,
							  ProductPagingRepository productPagingRepository,
							  ReviewRepository reviewRepository,
							  ProductImgRepository productImgRepository,
							  S3Upload s3Upload) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.productPagingRepository = productPagingRepository;
		this.dateUtils = dateUtils;
		this.env = env;
		this.productQnaRepository = productQnaRepository;
		this.productQnaAnswerRepository = productQnaAnswerRepository;
		this.sellerServiceClient = sellerServiceClient;
		this.userServiceClient = userServiceClient;
		this.orderServiceClient = orderServiceClient;
		this.productWishRepository = productWishRepository;
		this.cartRepository = cartRepository;
		this.reviewRepository = reviewRepository;
		this.s3Upload=s3Upload;
		this.productImgRepository=productImgRepository;
	}

	@Override
	public Long saveProduct(ProductFormDto productFormDto) throws IOException {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		Product product = modelMapper.map(productFormDto, Product.class);

		Long categoryId = productFormDto.getCategoryId();
		Optional<Category> category = categoryRepository.findById(categoryId);

		int cnt = 0;
		for(MultipartFile multipartFile : productFormDto.getImages()){
			String url = s3Upload.productUpload(multipartFile);
			if(cnt==0){
				product.setProductMainImgSrc(url);
			}else {
				ProductImg img = ProductImg.builder()
						.product(product)
						.productImgSrc(url)
						.build();
				productImgRepository.save(img);
			}
			cnt++;
		}


		product.setCategory(category.get());
		product.setProductRegisterDate(dateUtils.transDate(env.getProperty("dateutils.format")));
		product.setSellerId(productFormDto.getSellerId());
		product.setProductWishCount(0);
		product.setProductSoldCount(0);
		product.setProductViewCount(0);
		return productRepository.save(product).getProductId();
	}

	@Override
	public Long updateProduct(ProductUpdateFormDto productUpdateFormDto) throws IOException {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		Optional<Product> product = productRepository.findById(productUpdateFormDto.getProductId());
		Optional<Category> category = categoryRepository.findById(productUpdateFormDto.getCategoryId());
		product.get().setProductName(productUpdateFormDto.getProductName());
		product.get().setCategory(category.get());
		product.get().setProductPrice(productUpdateFormDto.getProductPrice());
		product.get().setProductTotalStock(productUpdateFormDto.getProductTotalStock());
		product.get().setProductDetail(productUpdateFormDto.getProductDetail());
		product.get().setProductOriginPlace(productUpdateFormDto.getProductOriginPlace());
		product.get().setProductDeliveryCompany(productUpdateFormDto.getProductDeliveryCompany());
		product.get().setProductStatus(productUpdateFormDto.getProductStatus());
		product.get().setProductDetailShort(productUpdateFormDto.getProductDetailShort());
		product.get().setProductUpdateDate(dateUtils.transDate(env.getProperty("dateutils.format")));

		Optional<Product> changedProduct = productRepository.findById(productUpdateFormDto.getProductId());
		if(changedProduct.get().getProductStatus().equals("soldout")){
			changedProduct.get().setProductTotalStock(0);
		}

		//기존 이미지 삭제
		if(productUpdateFormDto.getDeleteImageIdList() != null){
			for(Long deleteImgId : productUpdateFormDto.getDeleteImageIdList()) {
				Optional<ProductImg> productImg = productImgRepository.findById(deleteImgId);
				productImgRepository.delete(productImg.get());
			}
		}
		//이미지 추가
		if(productUpdateFormDto.getAddImageList() != null){
			for(MultipartFile productImgs : productUpdateFormDto.getAddImageList()){
				String url = s3Upload.productUpload(productImgs);

				ProductImg productImg = ProductImg.builder()
						.product(product.get())
						.productImgSrc(url)
						.build();
				productImgRepository.save(productImg);
			}
		}
		//메인 이미지 변경
		if(productUpdateFormDto.getMainImage() != null){
			MultipartFile mainImage = productUpdateFormDto.getMainImage().get(0);
			String url = s3Upload.productUpload(mainImage);
			product.get().setProductMainImgSrc(url);
		}

		return product.get().getProductId();
	}

	@Override
	public Long deleteProduct(ProductDeleteDto productDeleteDto){
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		Optional<Product> product = productRepository.findById(productDeleteDto.getProductId());
		product.get().setProductStatus("deleted");
		product.get().setProductUpdateDate(dateUtils.transDate(env.getProperty("dateutils.format")));

		return product.get().getProductId();
	}

	@Override
	public ProductWishResultDto addProductToWishList(ProductWishFormDto productWishFormDto){
		ProductWishResultDto resultDto = new ProductWishResultDto();

		//Optional<User> user = userRepository.findById(productWishFormDto.getUserId());
		Long userId = productWishFormDto.getUserId();
		Optional<Product> product = productRepository.findById(productWishFormDto.getProductId());

		Optional<Wish> savedWish = productWishRepository.findWishByUserAndProduct(userId, product.get().getProductId());
		if(savedWish.isPresent()){
			resultDto.setWishId(savedWish.get().getWishId());
			resultDto.setIsCreated(false);
			return resultDto;
		}

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		Wish wish = modelMapper.map(productWishFormDto, Wish.class);
		wish.setUserId(userId);
		wish.setProduct(product.get());
		wish.setWishStatus(true);
		product.get().setProductWishCount(product.get().getProductWishCount() + 1);

		Long wishId = productWishRepository.save(wish).getWishId();
		resultDto.setWishId(wishId);
		resultDto.setIsCreated(true);

		return resultDto;
	}

	@Override
	public List<Long> cancelProductFromWishList(ProductWishCancelDto productWishCancelDto){

		for(Long wishId : productWishCancelDto.getWishId()) {
			Wish wish = productWishRepository.findById(wishId).get();
			wish.setWishStatus(false);

			Product product = productRepository.findById(wish.getProduct().getProductId()).get();
			product.setProductWishCount(product.getProductWishCount() - 1);
		}

		return productWishCancelDto.getWishId();
	}

	@Override
	public List<ProductWishResponse> getWishList(Long userId) {

		List<Wish> wishList =  productWishRepository.findWishListByUserId(userId);

		List<ProductWishResponse> productInfos = new ArrayList<>();
		for(Wish w : wishList){
			ProductWishResponse productWishResponse = ProductWishResponse.builder()
					.wistId(w.getWishId())
					.productId(w.getProduct().getProductId())
					.productName(w.getProduct().getProductName())
					.productMainImgSrc(w.getProduct().getProductMainImgSrc())
					.productDetail(w.getProduct().getProductDetail())
					.productDetailShort(w.getProduct().getProductDetailShort())
					.productOriginPlace(w.getProduct().getProductOriginPlace())
					.productPrice(w.getProduct().getProductPrice())
					.build();

			productInfos.add(productWishResponse);
		}

		return productInfos;
	}

	@Override
	public ProductDetailResponse findProductDetail(Long productId, Long userId) {
		Product product = productRepository.findById(productId).get();
		product.setProductViewCount(product.getProductViewCount()+1);
		Long sellerId = productRepository.findById(productId).get().getSellerId();
		SellerClientSellerDetailResponse sellerClientSellerDetailResponse = sellerServiceClient.findBySellerId(sellerId);

		ProductDetailResponse productDetailResponse = new ProductDetailResponse(product, sellerClientSellerDetailResponse);
		productDetailResponse.setProductViewCount(productDetailResponse.getProductViewCount()+1);
		if(userId != null){
			Optional<Wish> savedWish = productWishRepository.findWishByUserAndProduct(userId, productId);
			if(savedWish.isPresent()){
				productDetailResponse.setProductWishStatus(true);
			}

			Optional<Cart> savedCart = cartRepository.findNotDeletedCartByProduct(productId, userId);
			if(savedCart.isPresent()){
				productDetailResponse.setProductCartStatus(true);
			}
		}

		List<ProductImg> productImgList = productImgRepository.findByProduct(product);
		List<ProductImageResponse> productImgSrcList = new ArrayList<>();
		for(ProductImg productImg : productImgList){
			ProductImageResponse productImageResponse = new ProductImageResponse();
			productImageResponse.setProductImgId(productImg.getProductImgId());
			productImageResponse.setProductImgSrc(productImg.getProductImgSrc());
			productImgSrcList.add(productImageResponse);
		}
		productDetailResponse.setProductImageList(productImgSrcList);

		List<Review> reviewList = reviewRepository.findReviewByProduct(product);
		productDetailResponse.setReviewCount(reviewList.size());
		productDetailResponse.setReviewRate(0.0);
		if(reviewList.size() > 0) {
			Integer reviewSum = 0;
			for (Review review : reviewList) {
				reviewSum += review.getReviewRate();
			}
			productDetailResponse.setReviewRate((double) reviewSum / reviewList.size());
		}

		return productDetailResponse;
	}

	@Override
	public List<ProductSelectionResponse> getAllProductListOrderByNewest(Long userId, Integer pageNumber){
		PageRequest pageRequest = PageRequest.of(pageNumber, 8, Sort.by("productRegisterDate").descending());

		Page<Product> productList = productPagingRepository.findAllProductOrderByNewest(pageRequest);
		int totalPage = productList.getTotalPages();
		Long totalElements = productList.getTotalElements();

		PageVo pageVo = PageVo.builder()
				.totalPage(totalPage)
				.nowPage(pageNumber)
				.totalElement(totalElements)
				.build();

		return setProductSelectResponse(productList, userId, pageVo);
	}

	@Override
	public List<ProductSelectionResponse> getProductsListByHighPrice(Long userId, Integer pageNumber) {
		PageRequest pageRequest = PageRequest.of(pageNumber,8, Sort.by("productPrice").descending());

		Page<Product> productList =  productPagingRepository.findProductListByHighPrice(pageRequest);
		int totalPage = productList.getTotalPages();
		Long totalElements = productList.getTotalElements();

		PageVo pageVo = PageVo.builder()
				.totalPage(totalPage)
				.nowPage(pageNumber)
				.totalElement(totalElements)
				.build();

		return setProductSelectResponse(productList, userId, pageVo);
	}

	@Override
	public List<ProductSelectionResponse> getProductsListByLowPrice(Long userId, Integer pageNumber) {
		PageRequest pageRequest = PageRequest.of(pageNumber,8, Sort.by("productPrice").ascending());

		Page<Product> productList =  productPagingRepository.findProductListByLowPrice(pageRequest);
		int totalPage = productList.getTotalPages();
		Long totalElements = productList.getTotalElements();

		PageVo pageVo = PageVo.builder()
				.totalPage(totalPage)
				.nowPage(pageNumber)
				.totalElement(totalElements)
				.build();

		return setProductSelectResponse(productList, userId, pageVo);
	}

	@Override
	public List<ProductSelectionResponse> getMainProductsBySoldCount(Long userId){
		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("productSoldCount").descending());

		Page<Product> productList = productPagingRepository.findProductBySoldCount(pageRequest);
		int totalPage = productList.getTotalPages();
		Long totalElements = productList.getTotalElements();

		PageVo pageVo = PageVo.builder()
				.totalPage(totalPage)
				.nowPage(0)
				.totalElement(totalElements)
				.build();

		return setProductSelectResponse(productList, userId, pageVo);
	}

	@Override
	public List<ProductSelectionResponse> getProductsBySoldCount(Long userId, Integer pageNumber) {
		PageRequest pageRequest = PageRequest.of(pageNumber,8, Sort.by("productSoldCount").descending());

		Page<Product> productList =  productPagingRepository.findProductBySoldCount(pageRequest);
		int totalPage = productList.getTotalPages();
		Long totalElements = productList.getTotalElements();

		PageVo pageVo = PageVo.builder()
				.totalPage(totalPage)
				.nowPage(pageNumber)
				.totalElement(totalElements)
				.build();

		return setProductSelectResponse(productList, userId, pageVo);
	}

	@Override
	public List<ProductSelectionResponse> getProductListBySellerNewest(Long userId, Long sellerId, Integer pageNumber){
		PageRequest pageRequest = PageRequest.of(pageNumber, 8, Sort.by("productRegisterDate").descending());

		Page<Product> productList =  productPagingRepository.findProductBySellerNewest(pageRequest, sellerId);
		int totalPage = productList.getTotalPages();
		Long totalElements = productList.getTotalElements();

		PageVo pageVo = PageVo.builder()
				.totalPage(totalPage)
				.nowPage(pageNumber)
				.totalElement(totalElements)
				.build();

		return setProductSelectResponse(productList, userId, pageVo);
	}

	@Override
	public List<ProductSelectionResponse> getProductListByCategoryNewest(Long userId, String category, Integer pageNumber) {
		PageRequest pageRequest = PageRequest.of(pageNumber,8, Sort.by("productRegisterDate").descending());

		Page<Product> productList =  productPagingRepository.findProductsByCategoryNewest(pageRequest, category);
		int totalPage = productList.getTotalPages();
		Long totalElements = productList.getTotalElements();

		PageVo pageVo = PageVo.builder()
				.totalPage(totalPage)
				.nowPage(pageNumber)
				.totalElement(totalElements)
				.build();

		return setProductSelectResponse(productList, userId, pageVo);
	}

	@Override
	public List<ProductSelectionResponse> getProductListByCategoryHighest(Long userId, String category, Integer pageNumber) {
		PageRequest pageRequest = PageRequest.of(pageNumber,8, Sort.by("productPrice").descending());

		Page<Product> productList =  productPagingRepository.findProductByCategoryHighest(pageRequest, category);
		int totalPage = productList.getTotalPages();
		Long totalElements = productList.getTotalElements();

		PageVo pageVo = PageVo.builder()
				.totalPage(totalPage)
				.nowPage(pageNumber)
				.totalElement(totalElements)
				.build();

		return setProductSelectResponse(productList, userId, pageVo);
	}

	@Override
	public List<ProductSelectionResponse> getProductListByCategoryLowest(Long userId, String category, Integer pageNumber) {
		PageRequest pageRequest = PageRequest.of(pageNumber,8, Sort.by("productPrice").ascending());

		Page<Product> productList =  productPagingRepository.findProductByCategoryLowest(pageRequest, category);
		int totalPage = productList.getTotalPages();
		Long totalElements = productList.getTotalElements();

		PageVo pageVo = PageVo.builder()
				.totalPage(totalPage)
				.nowPage(pageNumber)
				.totalElement(totalElements)
				.build();

		return setProductSelectResponse(productList, userId, pageVo);
	}

	@Override
	public List<ProductSelectionResponse> getProductsByCategorySoldCount(Long userId, String category, Integer pageNumber){
		PageRequest pageRequest = PageRequest.of(pageNumber,8, Sort.by("productSoldCount").descending());

		Page<Product> productList =  productPagingRepository.findProductByCategorySoldCount(pageRequest, category);
		int totalPage = productList.getTotalPages();
		Long totalElements = productList.getTotalElements();

		PageVo pageVo = PageVo.builder()
				.totalPage(totalPage)
				.nowPage(pageNumber)
				.totalElement(totalElements)
				.build();

		return setProductSelectResponse(productList, userId, pageVo);
	}

	@Override
	public List<ProductSelectionResponse> getSellingProductListBySellerNewest(Long userId, Long sellerId, Integer pageNumber){
		PageRequest pageRequest = PageRequest.of(pageNumber, 8, Sort.by("productRegisterDate").descending());

		Page<Product> productList =  productPagingRepository.findSellingProductBySellerNewest(pageRequest, sellerId);
		int totalPage = productList.getTotalPages();
		Long totalElements = productList.getTotalElements();

		PageVo pageVo = PageVo.builder()
				.totalPage(totalPage)
				.nowPage(pageNumber)
				.totalElement(totalElements)
				.build();

		return setProductSelectResponse(productList, userId, pageVo);
	}

	@Override
	public List<ProductSelectionResponse> getPauseProductListBySellerNewest(Long userId, Long sellerId, Integer pageNumber){
		PageRequest pageRequest = PageRequest.of(pageNumber, 8, Sort.by("productRegisterDate").descending());

		Page<Product> productList =  productPagingRepository.findPauseProductBySellerNewest(pageRequest, sellerId);
		int totalPage = productList.getTotalPages();
		Long totalElements = productList.getTotalElements();

		PageVo pageVo = PageVo.builder()
				.totalPage(totalPage)
				.nowPage(pageNumber)
				.totalElement(totalElements)
				.build();

		return setProductSelectResponse(productList, userId, pageVo);
	}

	@Override
	public List<ProductQnAResponse> findProductQnAList(Long productId){
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		Optional<Product> product = productRepository.findById(productId);
		List<ProductQna> productQnas = productQnaRepository.findByProduct(product.get());

		List<ProductQnAResponse> responses = new ArrayList<>();

		for(ProductQna productQna : productQnas){
			ProductQnAResponse response = ProductQnAResponse.builder()
					.productQnaStatus(productQna.getProductQnaStatus())
					.productQnaCreatedAt(productQna.getProductQnaCreatedAt())
					.productQnaContent(productQna.getProductQnaContent())
					.productQnaId(productQna.getProductQnaId())
					.productQnaModifiedAt(productQna.getProductQnaModifiedAt())
					.userName(userServiceClient.findUserNameByUserId(productQna.getUserId()).getUserName())
					.userProfileImg(userServiceClient.findUserNameByUserId(productQna.getUserId()).getUserProfileImg())
					.build();
			if(productQna.getProductQnaStatus().equals("waiting")){
				responses.add(response);
				continue;
			}
			if(productQna.getProductQnaStatus().equals("deleted")){
				continue;
			}
			String answer =
					productQnaAnswerRepository
							.findByProductQna(productQna)
							.getProductQnaAnswerContent();
			response.setProductSellerAnswer(answer);
			responses.add(response);
		}

//		// QNA : QNA답변
//		Map<ProductQnAResponse, ProductQnaAnswerResponse> matching = new HashMap<>();
//		for(ProductQna productQna : productQnas){
//			ProductQnAResponse response = modelMapper.map(productQna,ProductQnAResponse.class);
//			if(productQna.getProductQnaStatus().equals("waiting")||productQna.getProductQnaStatus().equals("deleted")){
//				matching.put(response,null);
//			}
//			else{
//				ProductQnaAnswer productQnaAnswer = productQnaAnswerRepository.findByProductQna(productQna);
//				ProductQnaAnswerResponse productQnaAnswerResponse
//						= modelMapper.map(productQnaAnswer, ProductQnaAnswerResponse.class);
//				matching.put(response,productQnaAnswerResponse);
//			}
//		}

		return responses;
	}

	/**
	 * 상품별로 로그인한 사용자의 wish, cart 여부 조회 메서드
	 * @param productList, userId
	 * @return List<ProductSelectionResponse>
	 */
	private List<ProductSelectionResponse> setProductSelectResponse(Page<Product> productList, Long userId, PageVo pageVo){
		List<ProductSelectionResponse> productResponseList = new ArrayList<>();

		for(Product p : productList) {
			SellerClientSellerDetailResponse sellerClientSellerDetailResponse = sellerServiceClient.findBySellerId(p.getSellerId());

			ProductSelectionResponse pResponse = new ProductSelectionResponse(p, sellerClientSellerDetailResponse);
			pResponse.setPageVo(pageVo);

			List<Review> reviewList = reviewRepository.findReviewByProduct(p);
			pResponse.setProductReviewCount(reviewList.size());
			pResponse.setReviewRate(0.0);
			if(reviewList.size() > 0) {
				Integer reviewSum = 0;
				for (Review review : reviewList) {
					reviewSum += review.getReviewRate();
				}
				pResponse.setReviewRate((double) reviewSum / reviewList.size());
			}

			if(userId != null){
				Optional<Wish> savedWish = productWishRepository.findWishByUserAndProduct(userId, p.getProductId());
				if(savedWish.isPresent()){
					pResponse.setProductWishStatus(true);
				}

				Optional<Cart> savedCart = cartRepository.findNotDeletedCartByProduct(p.getProductId(), userId);
				if(savedCart.isPresent()){
					pResponse.setProductCartStatus(true);
				}
			}

			productResponseList.add(pResponse);
		}

		return productResponseList;
	}

	/**
	 * 유저별로 리뷰 작성이 가능한 상품을 조회하는 메서드
	 * @param userId
	 * @return List<ProductReviewResponse>
	 */
	@Override
	public List<ProductReviewResponse> getProductsWithoutReview(Long userId) {

		List<ProductReviewResponse> productReviewResponses = new ArrayList<>();

		List<OrdersByUserResponse> orders = orderServiceClient.findProductWithoutReview(userId);
		for(OrdersByUserResponse o : orders){
			List<OrderClientOrderProductIdResponse> orderProducts = orderServiceClient.findByOrdersId(o.getOrdersId());
			SellerClientSellerDetailResponse sellerClientSellerDetailResponse = sellerServiceClient.findBySellerId(o.getSellerId());

			for(OrderClientOrderProductIdResponse orderProduct : orderProducts){
				Optional<Review> review = reviewRepository.findReviewByOrderProductId(orderProduct.getOrderProductId());

				if(!review.isPresent()) {
					Optional<Product> product = productRepository.findById(orderProduct.getProductId());
					ProductReviewResponse productReviewResponse = ProductReviewResponse.builder()
							.productName(product.get().getProductName())
							.productMainImgSrc(product.get().getProductMainImgSrc())
							.productOriginPlace(product.get().getProductOriginPlace())
							.sellerShopName(sellerClientSellerDetailResponse.getSellerShopName())
							.productId(orderProduct.getProductId())
							.orderProductId(orderProduct.getOrderProductId())
							.ordersDate(o.getOrdersDate())
							.build();
					productReviewResponses.add(productReviewResponse);
				}
			}
		}

		return productReviewResponses;
	}

	public void updateStockAndSoldCount(Object productStockDto){
		HashMap<String,Object> productStock = (HashMap<String,Object>) productStockDto;
		Long productId = Long.valueOf(String.valueOf(productStock.get("productId")));
		Integer productQty = Integer.valueOf(String.valueOf(productStock.get("productQty")));

		Product product = productRepository.findById(productId).get();
		product.setProductTotalStock(product.getProductTotalStock()-productQty);
		// 상품 판매 count 증가
		product.setProductSoldCount(product.getProductSoldCount()+1);
		// 재고 차감 시 재고가 0일 경우 product status를 soldout으로 변경
		if(product.getProductSoldCount()==0){
			product.setProductStatus("soldout");
		}
	}

	@Override
	public Boolean createProductQnA(UserQnaDto userQnaDto) {
		Optional<Product> product = productRepository.findById(userQnaDto.getProductId());
		log.info("product 정보  :  " + product.get().toString());
		ProductQna productQna = ProductQna.builder()
				.product(product.get())
				.userId(userQnaDto.getUserId())
				.productQnaContent(userQnaDto.getProductQnaContent())
				.productQnaCreatedAt(dateUtils.transDate(env.getProperty("dateutils.format")))
				.productQnaStatus("waiting")
				.sellerId(product.get().getSellerId())
				.build();
		ProductQna newQna = productQnaRepository.save(productQna);
		if (newQna == null) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	@Override
	public ProductQnAResultResponse findUserQna(Long userId, Integer pageNum) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		List<ProductQnAResponse> responses = new ArrayList<>();

		List<ProductQna> productQnas = productQnaRepository.findByUserId(userId);
		UserClientUserShortInfoResponse userClientUserShortInfoResponse = userServiceClient.findUserNameByUserId(userId);

		for (ProductQna productQna : productQnas) {
			ProductQnAResponse response = modelMapper.map(productQna, ProductQnAResponse.class);
			if(response.getProductQnaStatus().equals("deleted")) continue;
			if(response.getProductQnaStatus().equals("completed")){
				String answer =
						productQnaAnswerRepository
								.findByProductQna(productQna)
								.getProductQnaAnswerContent();
				response.setProductSellerAnswer(answer);
			}
			response.setUserName(userClientUserShortInfoResponse.getUserName());
			response.setUserProfileImg(userClientUserShortInfoResponse.getUserProfileImg());
			responses.add(response);
		}

		ProductQnAResultResponse resultResponse = new ProductQnAResultResponse();

		responses.sort((o1, o2) -> {
			int result = o2.getProductQnaCreatedAt().compareTo(o1.getProductQnaCreatedAt());
			return result;
		});

		int startIndex = pageNum*pageContentNumber;

		int size = responses.size();

		if(size<startIndex+pageContentNumber){
			resultResponse.setResponses(responses.subList(startIndex,size));
			resultResponse.setCurrentPageNum(pageNum);
			if(size%pageContentNumber!=0){
				resultResponse.setTotalPageNum((size/pageContentNumber)+1);
			}
			else{
				resultResponse.setTotalPageNum(size/pageContentNumber);
			}
			return resultResponse;
		}

		resultResponse.setResponses(responses.subList(startIndex,startIndex+pageContentNumber));
		resultResponse.setCurrentPageNum(pageNum);
		if(size%pageContentNumber!=0){
			resultResponse.setTotalPageNum((size/pageContentNumber)+1);
		}
		else{
			resultResponse.setTotalPageNum(size/pageContentNumber);
		}
		return resultResponse;
	}

	/**
	 * 유저의 질의를 수정하는 메서드
	 * @param userQnaUpdateDto
	 * @return
	 */
	@Override
	public Boolean updateUserQna(UserQnaUpdateDto userQnaUpdateDto) {
		Optional<ProductQna> productQna = productQnaRepository.findById(userQnaUpdateDto.getProductQnaId());
		productQna.get().setProductQnaContent(userQnaUpdateDto.getProductQnaContent());
		productQna.get().setProductQnaModifiedAt(dateUtils.transDate(env.getProperty("dateutils.format")));
		if (productQna.get().getProductQnaContent().equals(userQnaUpdateDto.getProductQnaContent())) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	@Override
	public Boolean deleteUserQna(Long productQnaId) {
		Optional<ProductQna> productQna = productQnaRepository.findById(productQnaId);
		productQna.get().setProductQnaStatus("deleted");
		if (productQna.get().getProductQnaStatus().equals("deleted")) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
     * 셀러의 마이페이지를 조회하는 메서드
     * @param sellerMypageDto
     * @return
     */
	@Override
    public SellerMypageResponse findSellerMypage(SellerMypageDto sellerMypageDto){

        SellerMypageResponse response = new SellerMypageResponse();

        List<SellerRecentReviewResponse> reviews = findReviewMypage(sellerMypageDto.getSellerId());

        List<SellerPopularProductResponse> popularProducts = findPopularProduct(sellerMypageDto.getSellerId());

        response.setReviews(reviews);
        response.setPopularProducts(popularProducts);

//        List<OrderProduct> orderProducts = orderProductRepository.findBySellerIdAndOrderProductDateBetween(
//                sellerMypageDto.getSellerId(),
//                sellerMypageDto.getStartDate(),
//                sellerMypageDto.getEndDate());

        /*
                해당 기간 총 수익
         */
        int totalPrice = 0; // 기간 총 수익

        /*
                일간 수익 조회
         */
        String startDate = sellerMypageDto.getStartDate().substring(0,10);
        String endDate = sellerMypageDto.getEndDate().substring(0,10);
        String nextDate = startDate;

        List<Integer> dayPrices = new ArrayList<>();
        Set<Long> orderList = new HashSet<>(); // 기간 총 주문을 담은 set
        List<Integer> dayOrderCounts = new ArrayList<>();

        while(true){
            int dayPrice = 0;
            int dayOrderCount = 0;
            List<OrderClientSellerIdAndDateResponse> orderProductList =
                    orderServiceClient.findBySellerIdAndOrderProductDateStartingWith(
                            sellerMypageDto.getSellerId(), nextDate);
            for(OrderClientSellerIdAndDateResponse orderProduct : orderProductList){
                if(!orderList.contains(orderProduct.getOrdersId())){
                    orderList.add(orderProduct.getOrdersId());
                    dayOrderCount++;
                }
                dayPrice+=orderProduct.getOrderProductPrice()*orderProduct.getOrderProductQty();
            }
            dayPrices.add(dayPrice);
            dayOrderCounts.add(dayOrderCount);
            nextDate = dateUtils.nextDate(nextDate);
            if(nextDate.equals(endDate)){
                break;
            }
        }

        int dayPrice = 0;
        int dayOrderCount = 0;
		List<OrderClientSellerIdAndDateResponse> orderProductList =
				orderServiceClient.findBySellerIdAndOrderProductDateStartingWith(
                        sellerMypageDto.getSellerId(), nextDate);
        for(OrderClientSellerIdAndDateResponse orderProduct : orderProductList){
            if(!orderList.contains(orderProduct.getOrdersId())){
                orderList.add(orderProduct.getOrdersId());
                dayOrderCount++;
            }
            dayPrice+=orderProduct.getOrderProductPrice()*orderProduct.getOrderProductQty();
        }
        dayPrices.add(dayPrice);
        dayOrderCounts.add(dayOrderCount);
        for(Integer price : dayPrices){
            totalPrice += price;
        }

        response.setDayPrices(dayPrices);
        response.setTotalPrice(totalPrice);
        response.setTotalOrderCount(orderList.size());
        response.setDayOrderCount(dayOrderCounts);
        return response;
    }

    /**
     * 셀러 메인페이지에 최신 리뷰 4개 보여줄 메서드
     * @param sellerId
     * @return
     */
	@Override
    public List<SellerRecentReviewResponse> findReviewMypage(Long sellerId){
        List<SellerRecentReviewResponse> responses = new ArrayList<>();

        List<Review> reviews = reviewRepository.findBySellerIdOrderByReviewCreatedAtDesc(sellerId);
        if(reviews.size()<listNum){
            for(Review review : reviews){
                if(review.getReviewStatus().equals("deleted")) continue;
                Product product = review.getProduct();
                SellerRecentReviewResponse response = SellerRecentReviewResponse.builder()
                        .productImg(product.getProductMainImgSrc())
                        .productName(product.getProductName())
                        .reviewContent(review.getReviewContent())
                        .reviewRate(review.getReviewRate())
                        .reviewLikeCount(review.getReviewLikeCount())
                        .build();
                responses.add(response);
            }
        }
        else{
            for(Review review : reviews.subList(0,listNum)){
                if(review.getReviewStatus().equals("deleted")) continue;
                Product product = review.getProduct();
                SellerRecentReviewResponse response = SellerRecentReviewResponse.builder()
                        .productImg(product.getProductMainImgSrc())
                        .productName(product.getProductName())
                        .reviewContent(review.getReviewContent())
                        .reviewRate(review.getReviewRate())
                        .reviewLikeCount(review.getReviewLikeCount())
                        .build();
                responses.add(response);
            }
        }


        return responses;
    }

    /**
     * 셀러 메인페이지에 찜순 상품 4개 보여줄 메서드
     * @param sellerId
     * @return
     */
	@Override
    public List<SellerPopularProductResponse> findPopularProduct(Long sellerId){
        List<SellerPopularProductResponse> responses = new ArrayList<>();

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        List<Product> products = productRepository.findBySellerIdOrderByProductWishCountDesc(sellerId);

        for(Product product : products){
            SellerPopularProductResponse response =
                    modelMapper.map(product,SellerPopularProductResponse.class);
            responses.add(response);
        }
        if(responses.size()<listNum){
            return responses;
        }
        return responses.subList(0,listNum);
    }

    /**
     * 셀러가 가진 QNA 조회
     * @param sellerId
     * @return
     */
	@Override
    public SellerProductQnaResponseResult findSellerQnA(Long sellerId, Integer pageNumber){
        SellerProductQnaResponseResult sellerProductQnaResponseResult = new SellerProductQnaResponseResult();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		List<ProductQna> productQnas = productQnaRepository.findBySellerId(sellerId);
        List<SellerProductQnaResponse> sellerProductQnaResponses = new ArrayList<>();
        for(ProductQna productQna : productQnas){
            SellerProductQnaResponse response
                    = modelMapper.map(productQna,SellerProductQnaResponse.class);
            Product product
                    = productRepository.findById(productQna.getProduct().getProductId()).get();

            Long userId = productQna.getUserId();
			UserClientUserShortInfoResponse userClientUserShortInfoResponse = userServiceClient.findUserNameByUserId(userId);

            response.setProductImg(product.getProductMainImgSrc());
            response.setProductName(product.getProductName());
            response.setUserName(userClientUserShortInfoResponse.getUserName());
            response.setUserProfileImg(userClientUserShortInfoResponse.getUserProfileImg());
            if(productQna.getProductQnaStatus().equals("completed")){
                String answer = productQnaAnswerRepository
                        .findByProductQna(productQna)
                        .getProductQnaAnswerContent();
                response.setProductSellerAnswer(answer);
            }
            sellerProductQnaResponses.add(response);
        }

        sellerProductQnaResponses.sort((o1, o2) -> {
            int result = o2.getProductQnaCreatedAt().compareTo(o1.getProductQnaCreatedAt());
            return result;
        });

        int startIndex = pageNumber*pageContentNumber;

        int size = sellerProductQnaResponses.size();

        if(size<startIndex+pageContentNumber) {
            sellerProductQnaResponseResult
                    .setSellerProductQnaResponseList(sellerProductQnaResponses.subList(startIndex, size));
        }
        else{
            sellerProductQnaResponseResult
                    .setSellerProductQnaResponseList(
                            sellerProductQnaResponses.subList(startIndex,startIndex+pageContentNumber));
        }
        sellerProductQnaResponseResult.setCurrentPageNum(pageNumber);
        if(size%pageContentNumber!=0){
            sellerProductQnaResponseResult.setTotalPageNum((size/pageContentNumber)+1);
        }
        else{
            sellerProductQnaResponseResult.setTotalPageNum(size/pageContentNumber);
        }
        return sellerProductQnaResponseResult;
    }

    /**
     * 답변 생성하는 메서드
     * status
     * waiting(qna0) : 답변 대기
     * completed(qna1) : 답변 완료
     * deleted(qna2) : qna 삭제
     * @param sellerQnaDto
     */
	@Override
    public Boolean createQnaAnswer(SellerQnaDto sellerQnaDto){
        Optional<ProductQna> qna = productQnaRepository.findById(Long.valueOf(sellerQnaDto.getProductQnaId()));
        qna.get().setProductQnaStatus("completed");
        ProductQnaAnswer productQnaAnswer = ProductQnaAnswer.builder()
                .productQna(qna.get())
                .productQnaAnswerContent(sellerQnaDto.getProductQnaAnswerContent())
                .productQnaAnswerCreatedAt(dateUtils.transDate(env.getProperty("dateutils.format")))
                .build();
        ProductQnaAnswer qnaAnswer = productQnaAnswerRepository.save(productQnaAnswer);
        if(qnaAnswer==null){
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
