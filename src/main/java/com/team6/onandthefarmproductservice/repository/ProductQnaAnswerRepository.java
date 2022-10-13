package com.team6.onandthefarmproductservice.repository;

import org.springframework.data.repository.CrudRepository;

import com.team6.onandthefarmproductservice.entity.ProductQna;
import com.team6.onandthefarmproductservice.entity.ProductQnaAnswer;

public interface ProductQnaAnswerRepository extends CrudRepository<ProductQnaAnswer,Long> {
    ProductQnaAnswer findByProductQna(ProductQna productQna);
}
