package com.team6.onandthefarmproductservice.feignclient.service;

import com.team6.onandthefarmproductservice.entity.Cart;
import com.team6.onandthefarmproductservice.entity.Product;
import com.team6.onandthefarmproductservice.feignclient.vo.*;
import com.team6.onandthefarmproductservice.repository.CartRepository;
import com.team6.onandthefarmproductservice.repository.ProductQnaRepository;
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
public class ProductServiceClientServiceImpEX implements ProductServiceClientServiceEX {

    private final CartRepository cartRepository;

    private final ProductRepository productRepository;

    private final ProductQnaRepository productQnaRepository;

    public List<CartVo> findCartByUserId(Long userId){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        List<Cart> carts = cartRepository.findByUser(userId);
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
        return productRepository.findNotSellingProduct(sellerId);
    }

    public List<ProductVo> findSellingProduct(Long sellerId){
        return productRepository.findNotSellingProduct(sellerId);
    }

    public List<ProductQnaVo> findBeforeAnswerProductQna(Long sellerId){
        return productQnaRepository.findBeforeAnswerProductQna(sellerId);
    }
}
