package com.team6.onandthefarmproductservice.controller;

import com.team6.onandthefarmproductservice.dto.cart.CartDeleteDto;
import com.team6.onandthefarmproductservice.dto.cart.CartDto;
import com.team6.onandthefarmproductservice.dto.cart.CartIsActivatedDto;
import com.team6.onandthefarmproductservice.service.CartService;
import com.team6.onandthefarmproductservice.util.BaseResponse;
import com.team6.onandthefarmproductservice.vo.cart.*;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/user/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService){
        this.cartService = cartService;
    }

    @PostMapping("/add")
    @ApiOperation(value = "장바구니 추가")
    public ResponseEntity<BaseResponse> addCart(@ApiIgnore Principal principal, @RequestBody CartRequest cartRequest){

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        CartDto cartDto = modelMapper.map(cartRequest, CartDto.class);

        if(principal == null){
            BaseResponse baseResponse = BaseResponse.builder()
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .message("no authorization")
                    .build();
            return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
        }

        String[] principalInfo = principal.getName().split(" ");

        Long userId = Long.parseLong(principalInfo[0]);
        List<Long> cartIdList = cartService.addCart(cartDto, userId);

        BaseResponse baseResponse = BaseResponse.builder()
                .httpStatus(HttpStatus.CREATED)
                .message("cart add completed")
                .data(cartIdList)
                .build();

        if(cartIdList == null){
            baseResponse = BaseResponse.builder()
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .message("cart add failed")
                    .build();
            return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/set")
    @ApiOperation(value = "장바구니 수량을 정해진 개수만큼 저장")
    public ResponseEntity<BaseResponse> setCart(@ApiIgnore Principal principal, @RequestBody CartRequest cartRequest){

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        CartDto cartDto = modelMapper.map(cartRequest, CartDto.class);

        if(principal == null){
            BaseResponse baseResponse = BaseResponse.builder()
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .message("no authorization")
                    .build();
            return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
        }

        String[] principalInfo = principal.getName().split(" ");

        Long userId = Long.parseLong(principalInfo[0]);
        Long cartId = cartService.setCart(cartDto, userId);

        BaseResponse baseResponse = BaseResponse.builder()
                .httpStatus(HttpStatus.CREATED)
                .message("cart add completed")
                .data(cartId)
                .build();

        if(cartId == null){
            baseResponse = BaseResponse.builder()
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .message("cart add failed")
                    .build();
            return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(baseResponse, HttpStatus.OK);
    }

    @PutMapping
    @ApiOperation(value = "장바구니 유지 여부 수정")
    public ResponseEntity<BaseResponse> updateCartIsActivated(@RequestBody CartIsActivatedRequest cartIsActivatedRequest){

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        CartIsActivatedDto cartIsActivatedDto = modelMapper.map(cartIsActivatedRequest, CartIsActivatedDto.class);

        Long cartId = cartService.updateCartIsActivated(cartIsActivatedDto);

        BaseResponse baseResponse = BaseResponse.builder()
                .httpStatus(HttpStatus.CREATED)
                .message("cartIsActivated update completed")
                .data(cartId)
                .build();

        return new ResponseEntity(baseResponse, HttpStatus.CREATED);
    }

    @PutMapping("/delete")
    @ApiOperation(value = "장바구니 삭제")
    public ResponseEntity<BaseResponse> deleteCart(@ApiIgnore Principal principal, @RequestBody CartDeleteRequest cartDeleteRequest){

        if(principal == null){
            BaseResponse baseResponse = BaseResponse.builder()
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .message("no authorization")
                    .build();
            return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
        }

        String[] principalInfo = principal.getName().split(" ");
        Long userId = Long.parseLong(principalInfo[0]);

        CartDeleteDto cartDeleteDto = CartDeleteDto.builder()
                .cartList(cartDeleteRequest.getCartList()).build();

        List<Long> cartId = cartService.deleteCart(userId, cartDeleteDto);

        BaseResponse baseResponse = BaseResponse.builder()
                .httpStatus(HttpStatus.CREATED)
                .message("cart delete completed")
                .data(cartId)
                .build();

        return new ResponseEntity(baseResponse, HttpStatus.CREATED);
    }

    @GetMapping
    @ApiOperation(value = "장바구니 조회")
    public ResponseEntity<BaseResponse<CartResult>> selectCart(@ApiIgnore Principal principal, @RequestParam Integer pageNumber){

        if(principal == null){
            BaseResponse baseResponse = BaseResponse.builder()
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .message("no authorization")
                    .build();
            return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
        }

        String[] principalInfo = principal.getName().split(" ");
        Long userId = Long.parseLong(principalInfo[0]);

        CartResult cartResponses = cartService.selectCart(userId, pageNumber);

        BaseResponse baseResponse = BaseResponse.builder()
                .httpStatus(HttpStatus.CREATED)
                .message("cart select completed")
                .data(cartResponses)
                .build();

        return new ResponseEntity(baseResponse, HttpStatus.CREATED);
    }

}
