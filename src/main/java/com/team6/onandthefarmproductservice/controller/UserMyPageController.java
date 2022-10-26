package com.team6.onandthefarmproductservice.controller;

import com.team6.onandthefarmproductservice.service.ProductService;
import com.team6.onandthefarmproductservice.util.BaseResponse;
import com.team6.onandthefarmproductservice.vo.product.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequestMapping("/api/user/mypage")
public class UserMyPageController {

    private final ProductService productService;

    @Autowired
    public UserMyPageController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/wish")
    @ApiOperation(value = "사용자 별 위시리스트 조회")
    public ResponseEntity<BaseResponse<List<ProductWishResponse>>> getWishList(@ApiIgnore Principal principal) {

        if(principal == null){
            BaseResponse baseResponse = BaseResponse.builder()
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .message("no authorization")
                    .build();
            return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
        }

        String[] principalInfo = principal.getName().split(" ");
        Long userId = Long.parseLong(principalInfo[0]);

        List<ProductWishResponse> productInfos = productService.getWishList(userId);

        BaseResponse baseResponse = BaseResponse.builder()
                .httpStatus(HttpStatus.CREATED)
                .message("get wish list by user completed")
                .data(productInfos)
                .build();

        return new ResponseEntity(baseResponse, HttpStatus.OK);
    }

    @GetMapping("/review")
    @ApiOperation(value = "사용자 별로 작성 가능한 리뷰 조회")
    public ResponseEntity<BaseResponse<List<ProductReviewResponse>>> getWritableReviewList(
            @ApiIgnore Principal principal) {

        if(principal == null){
            BaseResponse baseResponse = BaseResponse.builder()
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .message("no authorization")
                    .build();
            return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
        }

        String[] principalInfo = principal.getName().split(" ");
        Long userId = Long.parseLong(principalInfo[0]);

        List<ProductReviewResponse> productsWithoutReview = productService.getProductsWithoutReview(userId);

        BaseResponse baseResponse = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("get writable reviews completed")
                .data(productsWithoutReview)
                .build();

        return new ResponseEntity(baseResponse, HttpStatus.OK);
    }

    /**
     * 10/24 상품 이미지 , 상품 이름 추가 지금은 null값
     * @param principal
     * @param pageNum
     * @return
     */
    @GetMapping("/QnA/{page-num}")
    @ApiOperation(value = "유저 질의 조회")
    public ResponseEntity<BaseResponse<ProductQnAResultResponse>> findUserQnA(
            @ApiIgnore Principal principal, @PathVariable("page-num") String pageNum) {

        if(principal == null){
            BaseResponse baseResponse = BaseResponse.builder()
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .message("no authorization")
                    .build();
            return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
        }

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        String[] principalInfo = principal.getName().split(" ");
        Long userId = Long.parseLong(principalInfo[0]);

        ProductQnAResultResponse responses = productService.findUserQna(userId,Integer.valueOf(pageNum));
        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("유저 QNA 조회")
                .data(responses)
                .build();
        return new ResponseEntity(response, HttpStatus.CREATED);
    }

}
