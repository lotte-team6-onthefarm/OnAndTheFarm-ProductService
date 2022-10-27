package com.team6.onandthefarmproductservice.feignclient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.onandthefarmproductservice.dto.product.OrderProductDto;
import com.team6.onandthefarmproductservice.dto.product.ProductStockDto;
import com.team6.onandthefarmproductservice.entity.Cart;
import com.team6.onandthefarmproductservice.entity.Product;
import com.team6.onandthefarmproductservice.entity.ProductQna;
import com.team6.onandthefarmproductservice.entity.ReservedOrder;
import com.team6.onandthefarmproductservice.feignclient.vo.*;
import com.team6.onandthefarmproductservice.kafka.ProductOrderChannelAdapter;
import com.team6.onandthefarmproductservice.repository.CartRepository;
import com.team6.onandthefarmproductservice.repository.ProductQnaRepository;
import com.team6.onandthefarmproductservice.repository.ProductRepository;
import com.team6.onandthefarmproductservice.repository.ReservedOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
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
    public ReservedOrder reservedOrder(String productList) {
        ReservedOrder reservedOrder = ReservedOrder.builder()
                .productList(productList)
                .createdDate(LocalDateTime.now())
                .expireTime(LocalDateTime.now().plus(3l, ChronoUnit.SECONDS))
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

        // ReservedStock 상태 변경
        reservedOrder.setStatus("CONFIRMED");
        // confirm 메시지 전송
        String message = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            message = objectMapper.writeValueAsString(productStockDtos);
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
}
