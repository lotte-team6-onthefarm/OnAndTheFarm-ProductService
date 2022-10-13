package com.team6.onandthefarmproductservice.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.team6.onandthefarmproductservice.entity.Product;
import com.team6.onandthefarmproductservice.entity.ProductQna;

public interface ProductQnaRepository extends CrudRepository<ProductQna,Long> {
    List<ProductQna> findBySeller(Long sellerId);

    List<ProductQna> findByProduct(Product product);

    List<ProductQna> findByUser(Long userId);
}
