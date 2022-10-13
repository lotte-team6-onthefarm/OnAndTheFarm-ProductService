package com.team6.onandthefarmproductservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team6.onandthefarmproductservice.dto.ProductDeleteDto;
import com.team6.onandthefarmproductservice.dto.ProductFormDto;
import com.team6.onandthefarmproductservice.dto.ProductUpdateFormDto;
import com.team6.onandthefarmproductservice.dto.ProductWishCancelDto;
import com.team6.onandthefarmproductservice.dto.ProductWishFormDto;
import com.team6.onandthefarmproductservice.dto.ProductWishResultDto;
import com.team6.onandthefarmproductservice.entity.Cart;
import com.team6.onandthefarmproductservice.entity.Category;
import com.team6.onandthefarmproductservice.entity.Product;
import com.team6.onandthefarmproductservice.entity.ProductQna;
import com.team6.onandthefarmproductservice.entity.Review;
import com.team6.onandthefarmproductservice.entity.Wish;
import com.team6.onandthefarmproductservice.feignclient.OrderServiceClient;
import com.team6.onandthefarmproductservice.feignclient.SellerServiceClient;
import com.team6.onandthefarmproductservice.repository.CartRepository;
import com.team6.onandthefarmproductservice.repository.CategoryRepository;
import com.team6.onandthefarmproductservice.repository.ProductPagingRepository;
import com.team6.onandthefarmproductservice.repository.ProductQnaAnswerRepository;
import com.team6.onandthefarmproductservice.repository.ProductQnaRepository;
import com.team6.onandthefarmproductservice.repository.ProductRepository;
import com.team6.onandthefarmproductservice.repository.ProductWishRepository;
import com.team6.onandthefarmproductservice.repository.ReviewRepository;
import com.team6.onandthefarmproductservice.util.DateUtils;
import com.team6.onandthefarmproductservice.vo.OrderProductResponse;
import com.team6.onandthefarmproductservice.vo.ProductDetailResponse;
import com.team6.onandthefarmproductservice.vo.ProductQnAResponse;
import com.team6.onandthefarmproductservice.vo.ProductReviewResponse;
import com.team6.onandthefarmproductservice.vo.ProductSelectionResponse;
import com.team6.onandthefarmproductservice.vo.ProductWishResponse;
import com.team6.onandthefarmproductservice.vo.ReviewableProductResponse;
import com.team6.onandthefarmproductservice.vo.SellerClientSellerDetailResponse;

@Service
@Transactional

public class ProductServiceImpl implements ProductService {

	private ProductRepository productRepository;
	private CategoryRepository categoryRepository;
	private ProductQnaRepository productQnaRepository;
	private ProductQnaAnswerRepository productQnaAnswerRepository;
	// private SellerRepository sellerRepository;
	private SellerServiceClient sellerServiceClient;
	private OrderServiceClient orderServiceClient;
	// private UserRepository userRepository;
	private ProductPagingRepository productPagingRepository;
	private ProductWishRepository productWishRepository;
	private CartRepository cartRepository;
	// private OrderRepository orderRepository;
	// private OrderProductRepository orderProductRepository;
	private ReviewRepository reviewRepository;
	private DateUtils dateUtils;
	private Environment env;

	@Autowired
	public ProductServiceImpl(ProductRepository productRepository,
							  CategoryRepository categoryRepository,
							  DateUtils dateUtils,
							  Environment env,
							  ProductQnaRepository productQnaRepository,
							  ProductQnaAnswerRepository productQnaAnswerRepository,
							  // SellerRepository sellerRepository,
							  SellerServiceClient sellerServiceClient,
							  OrderServiceClient orderServiceClient,
							  // UserRepository userRepository,
							  ProductWishRepository productWishRepository,
							  CartRepository cartRepository,
							  ProductPagingRepository productPagingRepository,
							  // OrderRepository orderRepository,
							  // OrderProductRepository orderProductRepository,
							  ReviewRepository reviewRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.productPagingRepository = productPagingRepository;
		this.dateUtils = dateUtils;
		this.env = env;
		this.productQnaRepository = productQnaRepository;
		this.productQnaAnswerRepository = productQnaAnswerRepository;
		// this.sellerRepository = sellerRepository;
		this.sellerServiceClient = sellerServiceClient;
		this.orderServiceClient = orderServiceClient;
		// this.userRepository = userRepository;
		this.productWishRepository = productWishRepository;
		this.cartRepository = cartRepository;
		// this.orderRepository = orderRepository;
		// this.orderProductRepository = orderProductRepository;
		this.reviewRepository = reviewRepository;
	}

	@Override
	public Long saveProduct(Long sellerId, ProductFormDto productFormDto){
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		Product product = modelMapper.map(productFormDto, Product.class);

		Long categoryId = productFormDto.getProductCategory();
		Optional<Category> category = categoryRepository.findById(categoryId);

		product.setCategory(category.get());
		product.setProductRegisterDate(dateUtils.transDate(env.getProperty("dateutils.format")));
		product.setSellerId(sellerId);
		product.setProductWishCount(0);
		product.setProductSoldCount(0);
		return productRepository.save(product).getProductId();
	}

	@Override
	public Long updateProduct(ProductUpdateFormDto productUpdateFormDto){
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		Optional<Product> product = productRepository.findById(productUpdateFormDto.getProductId());
		Optional<Category> category = categoryRepository.findById(productUpdateFormDto.getProductCategoryId());
		product.get().setProductName(productUpdateFormDto.getProductName());
		product.get().setCategory(category.get());
		product.get().setProductPrice(productUpdateFormDto.getProductPrice());
		product.get().setProductTotalStock(productUpdateFormDto.getProductTotalStock());
		//product.get().~~~~ 이미지 추가 해야함
		product.get().setProductDetail(productUpdateFormDto.getProductDetail());
		product.get().setProductOriginPlace(productUpdateFormDto.getProductOriginPlace());
		product.get().setProductDeliveryCompany(productUpdateFormDto.getProductDeliveryCompany());
		product.get().setProductStatus(productUpdateFormDto.getProductStatus());
		product.get().setProductDetailShort(productUpdateFormDto.getProductDetailShort());
		product.get().setProductUpdateDate(dateUtils.transDate(env.getProperty("dateutils.format")));

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

		// Optional<User> user = userRepository.findById(productWishFormDto.getUserId());
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
			productWishRepository.delete(wish);

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
		Long sellerId = productRepository.findById(productId).get().getSellerId();
		SellerClientSellerDetailResponse sellerClientSellerDetailResponse = sellerServiceClient.findBySellerId(sellerId);
		ProductDetailResponse productDetailResponse = new ProductDetailResponse(product, sellerClientSellerDetailResponse);
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
				// product 상품 설명 이미지 dto List 추가 필요
		return productDetailResponse;
	}

	@Override
	public List<ProductSelectionResponse> getAllProductListOrderByNewest(Long userId, Long sellerId, Integer pageNumber){
		PageRequest pageRequest = PageRequest.of(pageNumber, 8, Sort.by("productRegisterDate").descending());
		List<Product> productList = productPagingRepository.findAllProductOrderByNewest(pageRequest);
		if(userId == null) {
			SellerClientSellerDetailResponse sellerClientSellerDetailResponse = sellerServiceClient.findBySellerId(sellerId);
			return setProductSelectResponse(productList, userId, sellerClientSellerDetailResponse);
		}

		return setProductSelectResponse(productList, userId, null);

	}

	@Override
	public List<ProductSelectionResponse> getProductsListByHighPrice(Long userId, Long sellerId, Integer pageNumber) {
		PageRequest pageRequest = PageRequest.of(pageNumber,8, Sort.by("productPrice").descending());
		List<Product> productList =  productPagingRepository.findProductListByHighPrice(pageRequest);
		if(userId == null) {
			SellerClientSellerDetailResponse sellerClientSellerDetailResponse = sellerServiceClient.findBySellerId(userId);
			return setProductSelectResponse(productList, userId, sellerClientSellerDetailResponse);
		}

		return setProductSelectResponse(productList, userId, null);
	}

	@Override
	public List<ProductSelectionResponse> getProductsListByLowPrice(Long userId, Long sellerId, Integer pageNumber) {
		PageRequest pageRequest = PageRequest.of(pageNumber,8, Sort.by("productPrice").ascending());
		List<Product> productList =  productPagingRepository.findProductListByLowPrice(pageRequest);
		if(userId == null) {
			SellerClientSellerDetailResponse sellerClientSellerDetailResponse = sellerServiceClient.findBySellerId(userId);
			return setProductSelectResponse(productList, userId, sellerClientSellerDetailResponse);
		}

		return setProductSelectResponse(productList, userId, null);
	}

	@Override
	public List<ProductSelectionResponse> getProductsBySoldCount(Long userId, Long sellerId, Integer pageNumber) {
		PageRequest pageRequest = PageRequest.of(pageNumber,8, Sort.by("productSoldCount").descending());
		List<Product> productList = productPagingRepository.findProductBySoldCount(pageRequest);
		if(userId == null) {
			SellerClientSellerDetailResponse sellerClientSellerDetailResponse = sellerServiceClient.findBySellerId(userId);
			return setProductSelectResponse(productList, userId, sellerClientSellerDetailResponse);
		}

		return setProductSelectResponse(productList, userId, null);
	}

	@Override
	public List<ProductSelectionResponse> getProductListBySellerNewest(Long userId, Long sellerId, Integer pageNumber){
		PageRequest pageRequest = PageRequest.of(pageNumber, 8, Sort.by("productRegisterDate").descending());
		List<Product> productList = productPagingRepository.findProductBySellerNewest(pageRequest, sellerId);

		if(userId == null) {
			SellerClientSellerDetailResponse sellerClientSellerDetailResponse = sellerServiceClient.findBySellerId(userId);
			return setProductSelectResponse(productList, userId, sellerClientSellerDetailResponse);
		}

		return setProductSelectResponse(productList, userId, null);
	}

	@Override
	public List<ProductSelectionResponse> getProductListByCategoryNewest(Long userId, Long sellerId, String category, Integer pageNumber) {
		PageRequest pageRequest = PageRequest.of(pageNumber, 8, Sort.by("productRegisterDate").descending());
		List<Product> productList = productPagingRepository.findProductsByCategoryNewest(pageRequest, category);

		if (userId == null) {
			SellerClientSellerDetailResponse sellerClientSellerDetailResponse = sellerServiceClient.findBySellerId(
					userId);
			return setProductSelectResponse(productList, userId, sellerClientSellerDetailResponse);
		}

		return setProductSelectResponse(productList, userId, null);
	}

	@Override
	public List<ProductSelectionResponse> getProductListByCategoryHighest(Long userId, Long sellerId, String category, Integer pageNumber) {
		PageRequest pageRequest = PageRequest.of(pageNumber,8, Sort.by("productPrice").descending());
		List<Product> productList = productPagingRepository.findProductByCategoryHighest(pageRequest,category);
		if(userId == null) {
			SellerClientSellerDetailResponse sellerClientSellerDetailResponse = sellerServiceClient.findBySellerId(userId);
			return setProductSelectResponse(productList, userId, sellerClientSellerDetailResponse);
		}

		return setProductSelectResponse(productList, userId, null);
	}

	@Override
	public List<ProductSelectionResponse> getProductListByCategoryLowest(Long userId, Long sellerId, String category, Integer pageNumber) {
		PageRequest pageRequest = PageRequest.of(pageNumber,8, Sort.by("productPrice").ascending());
		List<Product> productList = productPagingRepository.findProductByCategoryLowest(pageRequest,category);
		if(userId == null) {
			SellerClientSellerDetailResponse sellerClientSellerDetailResponse = sellerServiceClient.findBySellerId(userId);
			return setProductSelectResponse(productList, userId, sellerClientSellerDetailResponse);
		}
		return setProductSelectResponse(productList, userId, null);
	}

	@Override
	public List<ProductSelectionResponse> getProductsByCategorySoldCount(Long userId, Long sellerId, String category, Integer pageNumber){
		PageRequest pageRequest = PageRequest.of(pageNumber,8, Sort.by("productSoldCount").descending());
		List<Product> productList = productPagingRepository.findProductByCategorySoldCount(pageRequest,category);

		if(userId == null) {
			SellerClientSellerDetailResponse sellerClientSellerDetailResponse = sellerServiceClient.findBySellerId(userId);
			return setProductSelectResponse(productList, userId, sellerClientSellerDetailResponse);
		}
		return setProductSelectResponse(productList, userId, null);
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
	private List<ProductSelectionResponse> setProductSelectResponse(List<Product> productList, Long userId, SellerClientSellerDetailResponse sellerClientSellerDetailResponse){

		List<ProductSelectionResponse> productResponseList = new ArrayList<>();

		for(Product p : productList) {
			ProductSelectionResponse pResponse = null;
			if(userId == null) {
				pResponse = new ProductSelectionResponse(p, sellerClientSellerDetailResponse);
			}
			else{
				sellerClientSellerDetailResponse = sellerServiceClient.findBySellerId(p.getSellerId());
				pResponse = new ProductSelectionResponse(p, sellerClientSellerDetailResponse);

				Optional<Wish> savedWish = productWishRepository.findWishByUserAndProduct(userId, p.getProductId());
				if(savedWish.isPresent()){
					pResponse.setProductWishStatus(true);
				}

				Optional<Cart> savedCart = cartRepository.findNotDeletedCartByProduct(p.getProductId(), userId);
				if(savedCart.isPresent()){
					pResponse.setProductCartStatus(true);
				}
			}
			List<Review> reviewList = reviewRepository.findReviewByProduct(p);
			if(reviewList.size() > 0) {
				Integer reviewSum = 0;
				for (Review review : reviewList) {
					reviewSum += review.getReviewRate();
				}
				pResponse.setReviewRate((double) (reviewSum / reviewList.size()));
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

		List<ReviewableProductResponse> orderList = orderServiceClient.findProductWithoutReview(userId);
		for(ReviewableProductResponse o : orderList){
			List<OrderProductResponse> orderProductList = orderServiceClient.findByOrdersId(o.getOrdersId());
			SellerClientSellerDetailResponse seller = sellerServiceClient.findBySellerId(o.getSellerId());

			for(OrderProductResponse orderProduct : orderProductList){
				Optional<Review> review = reviewRepository.findReviewByOrderProductId(orderProduct.getOrderProductId());

				if(!review.isPresent()) {
					Optional<Product> product = productRepository.findById(orderProduct.getProductId());
					ProductReviewResponse productReviewResponse = ProductReviewResponse.builder()
							.productName(product.get().getProductName())
							.productMainImgSrc(product.get().getProductMainImgSrc())
							.productOriginPlace(product.get().getProductOriginPlace())
							.sellerShopName(seller.getSellerShopName())
							.build();
					productReviewResponses.add(productReviewResponse);
				}
			}
		}

		return productReviewResponses;
	}

}
