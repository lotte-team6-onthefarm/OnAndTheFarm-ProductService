package com.team6.onandthefarmproductservice.service;

import com.team6.onandthefarmproductservice.dto.cart.CartDeleteDto;
import com.team6.onandthefarmproductservice.dto.cart.CartDto;
import com.team6.onandthefarmproductservice.dto.cart.CartIsActivatedDto;
import com.team6.onandthefarmproductservice.vo.cart.CartResponse;

import java.util.List;

public interface CartService {

    List<Long> addCart(CartDto cartDto, Long userId);
    Long updateCartIsActivated(CartIsActivatedDto cartIsActivatedDto);
    List<Long> deleteCart(CartDeleteDto cartDeleteDto);
    List<CartResponse> selectCart(Long userId);
    Long setCart(CartDto cartDto, Long userId);

}
