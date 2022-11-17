package com.team6.onandthefarmproductservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.team6.onandthefarmproductservice.entity.Wish;

public interface ProductWishRepository extends CrudRepository<Wish, Long> {

    @Query("select w from Wish w join fetch w.product where w.userId =:userId and w.wishStatus=true")
    List<Wish> findWishListByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndProduct_ProductId(Long userId,Long productId);

    @Query(value = "select w from Wish w join fetch w.product where w.userId =:userId",
            countQuery = "select count(w) from Wish w")
    Page<Wish> findWishListPageByUserId(PageRequest pageRequest, @Param("userId") Long userId);

    @Query("select w from Wish w join fetch w.product where w.userId =:userId and w.product.productId=:productId")
    Optional<Wish> findWishByUserAndProduct(@Param("userId")Long userId, @Param("productId")Long productId);

}
