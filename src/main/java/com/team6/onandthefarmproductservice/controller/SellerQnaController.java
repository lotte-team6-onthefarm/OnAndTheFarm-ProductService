package com.team6.onandthefarmproductservice.controller;

import com.team6.onandthefarmproductservice.dto.product.SellerQnaDto;
import com.team6.onandthefarmproductservice.service.ProductService;
import com.team6.onandthefarmproductservice.util.BaseResponse;
import com.team6.onandthefarmproductservice.vo.product.SellerProductQnaAnswerRequest;
import com.team6.onandthefarmproductservice.vo.product.SellerProductQnaResponseResult;
import io.swagger.annotations.Api;
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

@RestController
@Slf4j
@RequestMapping("/api/seller/QnA")
@Api(value = "셀러",description = "QNA status\n" +
        "     * waiting(qna0) : 답변 대기\n" +
        "     * completed(qna1) : 답변 완료\n" +
        "     * deleted(qna2) : qna 삭제")
public class SellerQnaController {

    private final ProductService productService;

    @Autowired
    public SellerQnaController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @ApiOperation(value = "셀러의 전체 질의 조회")
    public ResponseEntity<BaseResponse<SellerProductQnaResponseResult>> findSellerQnA (
            @ApiIgnore Principal principal, @RequestParam String pageNumber){

        if(principal == null){
            BaseResponse baseResponse = BaseResponse.builder()
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .message("no authorization")
                    .build();
            return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
        }

        String[] principalInfo = principal.getName().split(" ");
        Long sellerId = Long.parseLong(principalInfo[0]);

        SellerProductQnaResponseResult productQnas
                = productService.findSellerQnA(sellerId,Integer.valueOf(pageNumber));
        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("OK")
                .data(productQnas)
                .build();
        return new ResponseEntity(response,HttpStatus.OK);
    }

    @PostMapping
    @ApiOperation(value = "셀러의 질의 처리")
    public ResponseEntity<BaseResponse> createSellerQnaAnswer (@RequestBody SellerProductQnaAnswerRequest request){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SellerQnaDto sellerQnaDto = modelMapper.map(request, SellerQnaDto.class);
        Boolean result = productService.createQnaAnswer(sellerQnaDto);
        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("OK")
                .data(result)
                .build();
        return new ResponseEntity(response,HttpStatus.OK);
    }
}
