package com.team6.onandthefarmproductservice.feignclient.service;

import com.team6.onandthefarmproductservice.entity.Cart;
import com.team6.onandthefarmproductservice.entity.Product;
import com.team6.onandthefarmproductservice.feignclient.vo.CartClientResponse;
import com.team6.onandthefarmproductservice.feignclient.vo.ProductClientResponse;
import com.team6.onandthefarmproductservice.repository.CartRepository;
import com.team6.onandthefarmproductservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceClientServiceImp implements ProductServiceClientService{

    private final CartRepository cartRepository;

    private final ProductRepository productRepository;

    public List<CartClientResponse> findByUserId(Long userId){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        List<Cart> carts = cartRepository.findByUser(userId);
        List<CartClientResponse> responses = new ArrayList<>();

        for(Cart cart : carts){
            CartClientResponse response = modelMapper.map(cart,CartClientResponse.class);
            response.setProductId(cart.getProduct().getProductId());
            responses.add(response);
        }

        return responses;
    }

    public ProductClientResponse findByProductId(Long productId){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Product product = productRepository.findById(productId).get();
        ProductClientResponse response = modelMapper.map(product, ProductClientResponse.class);
        response.setCategoryId(product.getCategory().getCategoryId());

        return response;
    }
}
