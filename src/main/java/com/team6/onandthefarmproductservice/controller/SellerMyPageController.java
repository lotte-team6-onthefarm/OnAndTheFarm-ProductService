package com.team6.onandthefarmproductservice.controller;

import com.team6.onandthefarmproductservice.dto.product.SellerMypageDto;
import com.team6.onandthefarmproductservice.service.ProductService;
import com.team6.onandthefarmproductservice.util.BaseResponse;
import com.team6.onandthefarmproductservice.vo.product.SellerMypageRequest;
import com.team6.onandthefarmproductservice.vo.product.SellerMypageResponse;
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
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/seller/mypage")
public class SellerMyPageController {

    private final ProductService productService;

    @Autowired
    public SellerMyPageController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @ApiOperation(value = "셀러의 메인페이지 조회")
    public ResponseEntity<BaseResponse<SellerMypageResponse>> findSellerMypage(
            @ApiIgnore Principal principal, @RequestParam Map<String,String> map){

        if(principal == null){
            BaseResponse baseResponse = BaseResponse.builder()
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .message("no authorization")
                    .build();
            return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
        }

        String[] principalInfo = principal.getName().split(" ");
        Long sellerId = Long.parseLong(principalInfo[0]);

        String startDate = map.get("startDate").substring(0,10)+" 00:00:00";
        String endDate = map.get("endDate").substring(0,10)+" 23:59:59";

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SellerMypageRequest sellerMypageRequest = SellerMypageRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
        SellerMypageDto sellerMypageDto = modelMapper.map(sellerMypageRequest, SellerMypageDto.class);
        sellerMypageDto.setSellerId(sellerId);

        SellerMypageResponse mypageResponse = productService.findSellerMypage(sellerMypageDto);

        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("OK")
                .data(mypageResponse)
                .build();

        return new ResponseEntity(response,HttpStatus.OK);
    }
}
