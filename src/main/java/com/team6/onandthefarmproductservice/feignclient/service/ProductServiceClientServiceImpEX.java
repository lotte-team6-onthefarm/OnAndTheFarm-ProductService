package com.team6.onandthefarmproductservice.feignclient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.onandthefarmproductservice.dto.product.KafkaConfirmOrderDto;
import com.team6.onandthefarmproductservice.dto.product.OrderProductDto;
import com.team6.onandthefarmproductservice.dto.product.ProductStockDto;
import com.team6.onandthefarmproductservice.entity.Cart;
import com.team6.onandthefarmproductservice.entity.Product;
import com.team6.onandthefarmproductservice.entity.ProductQna;
import com.team6.onandthefarmproductservice.entity.ReservedOrder;
import com.team6.onandthefarmproductservice.entity.Review;
import com.team6.onandthefarmproductservice.entity.Wish;
import com.team6.onandthefarmproductservice.feignclient.vo.*;
import com.team6.onandthefarmproductservice.kafka.ProductOrderChannelAdapter;
import com.team6.onandthefarmproductservice.repository.CartRepository;
import com.team6.onandthefarmproductservice.repository.ProductQnaRepository;
import com.team6.onandthefarmproductservice.repository.ProductRepository;
import com.team6.onandthefarmproductservice.repository.ProductWishRepository;
import com.team6.onandthefarmproductservice.repository.ReservedOrderRepository;
import com.team6.onandthefarmproductservice.repository.ReviewRepository;
import com.team6.onandthefarmproductservice.vo.WishVo;
import com.team6.onandthefarmproductservice.vo.product.WishPageVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClientServiceImpEX implements ProductServiceClientServiceEX {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductQnaRepository productQnaRepository;
    private final ProductOrderChannelAdapter productOrderChannelAdapter;
    private final ReservedOrderRepository reservedOrderRepository;
    private final ProductWishRepository productWishRepository;
    private final ReviewRepository reviewRepository;

    public List<CartVo> findCartByUserId(Long userId){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        List<Cart> carts = cartRepository.findByUserId(userId);
        List<CartVo> responses = new ArrayList<>();

        for(Cart cart : carts){
            CartVo response = modelMapper.map(cart,CartVo.class);
            response.setProductId(cart.getProduct().getProductId());
            responses.add(response);
        }

        return responses;
    }

    public ProductVo findByProductId(Long productId){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Product product = productRepository.findById(productId).get();
        ProductVo response = modelMapper.map(product, ProductVo.class);
        response.setCategoryId(product.getCategory().getCategoryId());

        return response;
    }

    public List<ProductVo> findNotSellingProduct(Long sellerId){
        List<ProductVo> productVoList = new ArrayList<>();

        List<Product> products = productRepository.findNotSellingProduct(sellerId);
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        for(Product product : products){
            ProductVo productVo = modelMapper.map(product,ProductVo.class);
            productVo.setCategoryId(product.getCategory().getCategoryId());
            productVoList.add(productVo);
        }

        return productVoList;
    }

    public List<ProductVo> findSellingProduct(Long sellerId){
        List<ProductVo> productVoList = new ArrayList<>();

        List<Product> products =  productRepository.findSellingProduct(sellerId);
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        for(Product product : products){
            ProductVo productVo = modelMapper.map(product,ProductVo.class);
            productVo.setCategoryId(product.getCategory().getCategoryId());
            productVoList.add(productVo);
        }

        return productVoList;
    }

    public List<ProductQnaVo> findBeforeAnswerProductQna(Long sellerId){
        List<ProductQnaVo> productQnaVoList = new ArrayList<>();

        List<ProductQna> productQnas = productQnaRepository.findBeforeAnswerProductQna(sellerId);
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        for(ProductQna productQna : productQnas){
            ProductQnaVo productQnaVo = modelMapper.map(productQna,ProductQnaVo.class);
            productQnaVo.setProductId(productQna.getProduct().getProductId());
            productQnaVoList.add(productQnaVo);
        }

        return productQnaVoList;
    }

    /**
     * 주문 예약 테이블에 주문 정보를 db에 저장하는 메서드(try)
     * @param productList
     * @return
     */
    public ReservedOrder reservedOrder(String productList,String orderSerial) {
        ReservedOrder reservedOrder = ReservedOrder.builder()
                .productList(productList)
                .orderSerial(orderSerial)
                .createdDate(LocalDateTime.now())
                .expireTime(LocalDateTime.now().plus(10l, ChronoUnit.SECONDS))
                .idempoStatus(false)
                .build();
        return reservedOrderRepository.save(reservedOrder);
    }

    /**
     * 주문 확정(confirm)을 처리하는 메서드
     * @param id : 예약 테이블에 저장된 예약된 주문 정보의 pk값
     * @return
     */
    public Boolean confirmOrder(Long id) {
        ReservedOrder reservedOrder = reservedOrderRepository.findById(id).get();
        reservedOrder.validate(); // 예약 정보의 유효성 검증

        KafkaConfirmOrderDto kafkaConfirmOrderDto = new KafkaConfirmOrderDto();

        List<ProductStockDto> productStockDtos = new ArrayList<>(); // 제품 재고 및 판매 카운트의 메시지를 위한 리스트

        List<OrderProductDto> orderProductDtoList = new ArrayList<>();
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            orderProductDtoList
                    = objectMapper.readValue(reservedOrder.getProductList(), List.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }

        for(Object orderProduct : orderProductDtoList){
            HashMap<String,Object> orderProductDto = (HashMap<String, Object>) orderProduct;
            Long productId = Long.valueOf(String.valueOf(orderProductDto.get("productId")));
            Product product = productRepository.findById(productId).get();
            ProductStockDto productStockDto = ProductStockDto.builder()
                    .productId(product.getProductId())
                    .productQty((Integer)orderProductDto.get("productQty"))
                    .build();
            productStockDtos.add(productStockDto);
//            // 상품 재고 차감
//            product.setProductTotalStock(product.getProductTotalStock()-(Integer)orderProductDto.get("productQty"));
//            // 상품 판매 count 증가
//            product.setProductSoldCount(product.getProductSoldCount()+1);
        }
        kafkaConfirmOrderDto.setOrderSerial(reservedOrder.getOrderSerial());
        kafkaConfirmOrderDto.setProductStockDtos(productStockDtos);

        // ReservedStock 상태 변경
        reservedOrder.setStatus("CONFIRMED");
        // confirm 메시지 전송
        String message = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            message = objectMapper.writeValueAsString(kafkaConfirmOrderDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        productOrderChannelAdapter.producer(message);
        return Boolean.TRUE;
    }

    public void cancelOrder(Long id) {
        ReservedOrder reservedOrder = reservedOrderRepository.findById(id).get();
        reservedOrder.setStatus("CANCEL");
        log.info("Cancel Stock :" + id);
    }

    public List<WishVo> getWishListByMemberId(Long memberId){
        PageRequest pageRequest = PageRequest.of(0, 8, Sort.by("wishId").descending());
        WishPageVo wishPageVo = WishPageVo.builder()
                .pageNumber(0)
                .pageRequest(pageRequest)
                .build();

        Page<Wish> wishs = productWishRepository.findWishListPageByUserId(wishPageVo.getPageRequest(), memberId);
        List<WishVo> wishVos = new ArrayList<>();
        for (Wish wish : wishs) {
            WishVo wishVo = WishVo.builder()
                    .wishId(wish.getWishId())
                    .productId(wish.getProduct().getProductId())
                    .userId(wish.getUserId())
                    .wishStatus(wish.getWishStatus())
                    .totalPage(wishs.getTotalPages())
                    .nowPage(wishPageVo.getPageNumber())
                    .totalElement(wishs.getTotalElements())
                    .build();
            wishVos.add(wishVo);
        }
        return wishVos;
    }

    public ProductVo getProductVoByProductId(Long productId){
        Product product = productRepository.findProductByProductId(productId);
        ProductVo productVo = ProductVo.builder()
                .productId(product.getProductId())
                .categoryId(product.getCategory().getCategoryId())
                .sellerId(product.getSellerId())
                .productName(product.getProductName())
                .productPrice(product.getProductPrice())
                .productTotalStock(product.getProductTotalStock())
                .productMainImgSrc(product.getProductMainImgSrc())
                .productDetail(product.getProductDetail())
                .productDetailShort(product.getProductDetailShort())
                .productOriginPlace(product.getProductOriginPlace())
                .productDeliveryCompany(product.getProductDeliveryCompany())
                .productRegisterDate(product.getProductRegisterDate())
                .productUpdateDate(product.getProductUpdateDate())
                .productStatus(product.getProductStatus())
                .productWishCount(product.getProductWishCount())
                .productSoldCount(product.getProductSoldCount())
                .productDetail(product.getProductDetail())
                .productViewCount(product.getProductViewCount())
                .build();
        return productVo;
    }

    @Override
    public ReviewInfoToExbt getReviewInfoByProductId(Long productId){
        Product product = productRepository.findProductByProductId(productId);
        List<Review> reviews = reviewRepository.findReviewByProduct(product);

        double reviewRate = 0.0;
        Integer reviewCount = reviews.size();

        if(reviews.size() > 0) {
            Integer reviewSum = 0;
            for (Review review : reviews) {
                reviewSum += review.getReviewRate();
            }
            reviewRate = (double) reviewSum / reviews.size();
        }

        ReviewInfoToExbt reviewInfoToExbt = ReviewInfoToExbt.builder()
                .reviewRate(reviewRate)
                .reviewCount(reviewCount)
                .build();

        return reviewInfoToExbt;
    }

    public boolean getWishByProductUserId(Long productId, Long userId){
        boolean isWishExist = productWishRepository.existsByUserIdAndProduct_ProductId(userId, productId);
        return isWishExist;
    }

    public boolean getCartByProductUserId(Long productId, Long userId){
        boolean isCartExist = cartRepository.existsByUserIdAndProduct_ProductId(userId, productId);
        return isCartExist;
    }
}
