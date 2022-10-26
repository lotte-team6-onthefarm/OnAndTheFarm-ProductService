package com.team6.onandthefarmproductservice.controller;

import com.team6.onandthefarmproductservice.dto.product.UserQnaDto;
import com.team6.onandthefarmproductservice.dto.product.UserQnaUpdateDto;
import com.team6.onandthefarmproductservice.service.ProductService;
import com.team6.onandthefarmproductservice.util.BaseResponse;
import com.team6.onandthefarmproductservice.vo.product.UserQnaRequest;
import com.team6.onandthefarmproductservice.vo.product.UserQnaUpdateRequest;
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
@RequestMapping("/api/user/QnA")
public class UserQnaController {

    private final ProductService productService;

    @Autowired
    public UserQnaController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ApiOperation(value = "유저 질의 생성")
    public ResponseEntity<BaseResponse> createQnA(@ApiIgnore Principal principal,
                                                  @RequestBody UserQnaRequest userQnaRequest) {

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

        UserQnaDto userQnaDto = modelMapper.map(userQnaRequest, UserQnaDto.class);
        userQnaDto.setUserId(userId);

        Boolean result = productService.createProductQnA(userQnaDto);
        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("OK")
                .data(result)
                .build();
        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    @PutMapping
    @ApiOperation(value = "유저 질의 수정")
    public ResponseEntity<BaseResponse<Boolean>> updateUserQnA(
            @ApiIgnore Principal principal, @RequestBody UserQnaUpdateRequest userQnaUpdateRequest) {

        if(principal == null){
            BaseResponse baseResponse = BaseResponse.builder()
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .message("no authorization")
                    .build();
            return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
        }

        String[] principalInfo = principal.getName().split(" ");
        Long userId = Long.parseLong(principalInfo[0]);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserQnaUpdateDto userQnaUpdateDto = modelMapper.map(userQnaUpdateRequest, UserQnaUpdateDto.class);
        userQnaUpdateDto.setUserId(userId);
        Boolean result = productService.updateUserQna(userQnaUpdateDto);
        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("OK")
                .data(result)
                .build();
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PutMapping("/delete")
    @ApiOperation(value = "유저 질의 삭제")
    public ResponseEntity<BaseResponse<Boolean>> deleteUserQnA(@RequestParam Long productQnaId) {
        Boolean result = productService.deleteUserQna(productQnaId);

        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("OK")
                .data(result)
                .build();

        return new ResponseEntity(response, HttpStatus.OK);
    }
}
