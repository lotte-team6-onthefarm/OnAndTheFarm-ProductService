package com.team6.onandthefarmproductservice.feignclient.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.onandthefarmproductservice.ParticipantLink;
import com.team6.onandthefarmproductservice.entity.Product;
import com.team6.onandthefarmproductservice.entity.ReservedOrder;
import com.team6.onandthefarmproductservice.feignclient.service.ProductServiceClientServiceEX;
import com.team6.onandthefarmproductservice.feignclient.vo.*;
import com.team6.onandthefarmproductservice.repository.ProductRepository;
import com.team6.onandthefarmproductservice.service.ProductService;
import com.team6.onandthefarmproductservice.vo.WishVo;
import com.team6.onandthefarmproductservice.vo.product.WishPageVo;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProductServiceClientControllerEX {

    private final ProductServiceClientServiceEX productServiceClientService;

    private final ProductRepository productRepository;

    /**
     * 유저ID로 유저의 장바구니 목록을 가져오는 것
     * @param userId
     * @return
     */
    @GetMapping("/api/user/cart/product-service/{user-no}")
    public List<CartVo> findByUserId(@PathVariable("user-no") Long userId){
        return productServiceClientService.findCartByUserId(userId);
    }

    /**
     * 제품ID를 이용해 제품의 정보를 가져오는 것
     * @param productId
     * @return
     */
    @GetMapping("/api/user/product/product-service/{product-no}")
    public ProductVo findByProductId(@PathVariable("product-no") Long productId){
        return productServiceClientService.findByProductId(productId);
    }

    /**
     * 판매하지 않는 상품리스트 조회
     * @param sellerId
     * @return
     */
    @GetMapping("/api/user/product/product-service/no-selling/{seller-no}")
    List<ProductVo> findNotSellingProduct(@PathVariable("seller-no") Long sellerId){
        return productServiceClientService.findNotSellingProduct(sellerId);
    }
    @GetMapping("/api/user/product/product-service/selling/{seller-no}")
    List<ProductVo> findSellingProduct(@PathVariable("seller-no") Long sellerId){
        return productServiceClientService.findSellingProduct(sellerId);
    }

    @GetMapping("/api/user/product/product-service/qna/{seller-no}")
    List<ProductQnaVo> findBeforeAnswerProductQna(@PathVariable("seller-no") Long sellerId){
        return productServiceClientService.findBeforeAnswerProductQna(sellerId);
    }

    /**
     * order-service에서 product-service로 주문을 try하는 메서드
     * @param map : 주문 정보를 가진 객체 productIdList : List<OrderProduct>
     * @return participantLink객체를 리턴하며, confirm을 위한 url이 존재한다.
     */
    @PostMapping("/api/user/product/product-service/order-try")
    public ResponseEntity<ParticipantLink> orderTry(@RequestBody Map<String, Object> map){
        String productList = "";

        ObjectMapper objectMapper = new ObjectMapper();
        try{
            // 상품들의 정보를 직렬화
            productList = objectMapper.writeValueAsString(map.get("productIdList"));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        // 주문 예약 테이블에 예약 저장
        ReservedOrder reservedOrder = productServiceClientService.reservedOrder(productList);

        final ParticipantLink participantLink = buildParticipantLink(
                reservedOrder.getReservedOrderId(),
                reservedOrder.getExpireTime());
        return new ResponseEntity<>(participantLink, HttpStatus.CREATED);
    }

    /**
     * 재고 차감을 확정해주는 메서드
     * @param id
     * @return
     */
    @PutMapping("/api/user/product/product-service/order-try/{id}")
    public ResponseEntity<Void> confirmOrderAdjustment(@PathVariable Long id) {
        try {
            productServiceClientService.confirmOrder(id);
        } catch(IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * confirm시 사용될 url 및 timeExpire를 생성해주는 메서드
     * @param id : 예약 테이블에 저장되는 row의 pk값을 의미
     * @param expire : timeout 시간의미
     * @return location은 confirm시 사용될 url이다.ParticipantLink
     */
    private ParticipantLink buildParticipantLink(final Long id, LocalDateTime expire) {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
        return new ParticipantLink(location, expire);
    }


    /**
     * 주문 생성 시 오류가 날 경우 예약된 주문 정보를 cancel해주는 메서드
     * @param id
     * @return
     */
    @DeleteMapping("/api/user/product/product-service/order-try/{id}")
    public ResponseEntity<Void> cancelOrderAdjustment(@PathVariable Long id) {
        productServiceClientService.cancelOrder(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //member Id로 wish-list를 불러오는 메서드
    @PostMapping("/api/user/product/product-service/wish-list/{user-no}")
    public List<WishVo> findWishListByMemberId(@RequestBody WishPageVo wishPageVo, @PathVariable("user-no")Long memberId){
        return productServiceClientService.getWishListByMemberId(wishPageVo, memberId);
    }
    //product Id로 product 불러오는 메서드
    @GetMapping("/api/user/product/product-service/product/{product-no}")
    public ProductVo findProductByProductId(@PathVariable("product-no") Long productId){
        return productServiceClientService.getProductVoByProductId(productId);
    }
}
