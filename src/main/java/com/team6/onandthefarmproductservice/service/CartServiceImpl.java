package com.team6.onandthefarmproductservice.service;

import com.team6.onandthefarmproductservice.dto.cart.CartDeleteDto;
import com.team6.onandthefarmproductservice.dto.cart.CartDto;
import com.team6.onandthefarmproductservice.dto.cart.CartIsActivatedDto;
import com.team6.onandthefarmproductservice.entity.Cart;
import com.team6.onandthefarmproductservice.entity.Product;
import com.team6.onandthefarmproductservice.repository.CartRepository;
import com.team6.onandthefarmproductservice.repository.ProductRepository;
import com.team6.onandthefarmproductservice.util.DateUtils;
import com.team6.onandthefarmproductservice.vo.cart.CartInfoRequest;
import com.team6.onandthefarmproductservice.vo.cart.CartResponse;
import com.team6.onandthefarmproductservice.vo.cart.CartResult;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartServiceImpl implements CartService{

    private final int pageContentNumber = 8;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final DateUtils dateUtils;
    private final Environment env;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository,
                           ProductRepository productRepository,
                           DateUtils dateUtils,
                           Environment env){
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.dateUtils = dateUtils;
        this.env = env;
    }

    /**
     * 장바구니에 추가하는 메소드
     * @param cartDto
     * @return List<Long> (cartId 리스트)
     */
    @Override
    public List<Long> addCart(CartDto cartDto, Long userId) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        List<Long> cartIdList = new ArrayList<>();
        for(CartInfoRequest cartInfo : cartDto.getCartList()) {
            Optional<Product> product = productRepository.findById(cartInfo.getProductId());

            //해당 유저가 같은 상품(삭제되지않은상품)을 이미 카트에 등록했으면 수량만 증가
            Optional<Cart> cartEntity = cartRepository.findNotDeletedCartByProduct(product.get().getProductId(), userId);
            Cart savedCart = null;

            if(cartEntity.isPresent()){
                savedCart = cartEntity.get();
                savedCart.setCartQty(savedCart.getCartQty()+cartInfo.getCartQty());
            }
            else {
                Cart cart = new Cart();
                cart.setUserId(userId);
                cart.setProduct(product.get());
                cart.setCartQty(cartInfo.getCartQty());
                cart.setCartStatus(true);
                cart.setCartIsActivated(false);
                cart.setCartCreatedAt(dateUtils.transDate(env.getProperty("dateutils.format")));

                savedCart = cartRepository.save(cart);
            }

            cartIdList.add(savedCart.getCartId());
        }

        return cartIdList;
    }

    /**
     * 장바구니를 정해진 개수로 세팅하는 메서드
     * @param cartDto
     * @return Long
     */
    @Override
    public Long setCart(CartDto cartDto, Long userId) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        CartInfoRequest cartInfo = cartDto.getCartList().get(0);
        Optional<Product> product = productRepository.findById(cartInfo.getProductId());

        Optional<Cart> cart = cartRepository.findNotDeletedCartByProduct(product.get().getProductId(), userId);
        cart.get().setCartQty(cartInfo.getCartQty());

        return cart.get().getCartId();
    }

    /**
     * 장바구니의 유지 여부를 변경하는 메소드
     * @param cartIsActivatedDto
     * @return cartId
     */
    @Override
    public Long updateCartIsActivated(CartIsActivatedDto cartIsActivatedDto){

        Optional<Cart> cart = cartRepository.findById(cartIsActivatedDto.getCartId());
        cart.get().setCartIsActivated(cartIsActivatedDto.getCartIsActivated());

        return cart.get().getCartId();
    }

    /**
     * 장바구니를 삭제하는 메소드
     * @param cartDeleteDto
     * @return cartId
     */
    @Override
    public List<Long> deleteCart(CartDeleteDto cartDeleteDto) {

        for(Long cartId : cartDeleteDto.getCartList()) {
            Optional<Cart> cart = cartRepository.findById(cartId);
            cart.get().setCartStatus(false);
        }

        return cartDeleteDto.getCartList();
    }

    @Override
    public CartResult selectCart(Long userId, Integer pageNumber) {

        CartResult cartResult = new CartResult();

        List<Cart> carts = cartRepository.findNotDeletedCartByUserId(userId);
        int startIndex = pageNumber * pageContentNumber;
        int size = carts.size();

        List<CartResponse> cartResponses = getCartListPagination(size, startIndex, carts);

        cartResult.setCartResponseList(cartResponses);
        cartResult.setCurrentPageNum(pageNumber);
        cartResult.setTotalElementNum(size);
        if(size%pageContentNumber==0){
            cartResult.setTotalPageNum(size/pageContentNumber);
        }
        else{
            cartResult.setTotalPageNum((size/pageContentNumber)+1);
        }

        return cartResult;
    }

    public List<CartResponse> getCartListPagination(int size, int startIndex, List<Cart> cartList){
        List<CartResponse> cartResponseList = new ArrayList<>();

        if(size < startIndex){
            return cartResponseList;
        }

        if (size < startIndex + pageContentNumber) {
            for (Cart c : cartList.subList(startIndex, size)) {
                CartResponse cartResponse = CartResponse.builder()
                        .cartId(c.getCartId())
                        .cartIsActivated(c.getCartIsActivated())
                        .cartQty(c.getCartQty())
                        .productId(c.getProduct().getProductId())
                        .productName(c.getProduct().getProductName())
                        .productDeliveryCompany(c.getProduct().getProductDeliveryCompany())
                        .productMainImgSrc(c.getProduct().getProductMainImgSrc())
                        .productOriginPlace(c.getProduct().getProductOriginPlace())
                        .productPrice(c.getProduct().getProductPrice())
                        .productStatus(c.getProduct().getProductStatus())
                        .build();
                cartResponseList.add(cartResponse);
            }
            return cartResponseList;
        }
        for (Cart c : cartList.subList(startIndex, startIndex + pageContentNumber)) {
            CartResponse cartResponse = CartResponse.builder()
                    .cartId(c.getCartId())
                    .cartIsActivated(c.getCartIsActivated())
                    .productPrice(c.getProduct().getProductPrice())
                        .productStatus(c.getProduct().getProductStatus())
                        .build();
            cartResponseList.add(cartResponse);
        }
        return cartResponseList;
    }
}
