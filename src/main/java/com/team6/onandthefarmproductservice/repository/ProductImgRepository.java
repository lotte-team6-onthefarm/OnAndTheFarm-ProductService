package com.team6.onandthefarmproductservice.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.team6.onandthefarmproductservice.entity.Product;
import com.team6.onandthefarmproductservice.entity.ProductImg;

public interface ProductImgRepository extends CrudRepository<ProductImg, Long> {

    List<ProductImg> findByProduct(Product product);
}
