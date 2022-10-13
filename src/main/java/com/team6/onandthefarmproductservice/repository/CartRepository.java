package com.team6.onandthefarmproductservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team6.onandthefarmproductservice.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("select c from Cart c join fetch c.product p where c.user.userId =:userId and c.cartStatus is true")
    List<Cart> findNotDeletedCartByUserId(@Param("userId") Long userId);

    List<Cart> findByUser(Long userId);

    @Query("select c from Cart c where c.user.userId =:userId and c.product.productId=:productId and c.cartStatus is true")
    Optional<Cart> findNotDeletedCartByProduct(@Param("productId") Long productId, @Param("userId") Long userId);
}
