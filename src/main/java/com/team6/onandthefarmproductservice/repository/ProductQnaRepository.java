package com.team6.onandthefarmproductservice.repository;

import java.util.List;

import com.team6.onandthefarmproductservice.feignclient.vo.ProductQnaVo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.team6.onandthefarmproductservice.entity.Product;
import com.team6.onandthefarmproductservice.entity.ProductQna;
import org.springframework.data.repository.query.Param;

public interface ProductQnaRepository extends CrudRepository<ProductQna,Long> {
    List<ProductQna> findBySeller(Long sellerId);

    List<ProductQna> findByProduct(Product product);

    List<ProductQna> findByUser(Long userId);

    @Query("select q from ProductQna q where q.sellerId=:sellerId and q.productQnaStatus='waiting'")
    List<ProductQnaVo> findBeforeAnswerProductQna(@Param("sellerId") Long sellerId);
}
