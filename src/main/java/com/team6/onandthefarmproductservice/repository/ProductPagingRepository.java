
package com.team6.onandthefarmproductservice.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.team6.onandthefarmproductservice.entity.Product;

public interface ProductPagingRepository extends PagingAndSortingRepository<Product, Long> {

    @Query("select p from Product p join fetch p.category")
    List<Product> findAllProductOrderByNewest(PageRequest pageRequest);

    @Query("select p from Product p join fetch p.category")
    List<Product> findProductListByHighPrice(PageRequest pageRequest);

    @Query("select p from Product p join fetch p.category")
    List<Product> findProductListByLowPrice(PageRequest pageRequest);

    @Query("select p from Product p join fetch p.category")
    List<Product> findProductBySoldCount(PageRequest pageRequest);

    @Query("select p from Product p join fetch p.category where p.sellerId =:sellerId")
    List<Product> findProductBySellerNewest(PageRequest pageRequest, @Param("sellerId") Long sellerId);

    @Query("select p from Product p join fetch p.category where p.category.categoryName =:category")
    List<Product> findProductsByCategoryNewest(PageRequest pageRequest,@Param("category") String category);

    @Query("select p from Product p join fetch p.category where p.category.categoryName =:category")
    List<Product> findProductByCategoryHighest(PageRequest pageRequest, @Param("category") String category);

    @Query("select p from Product p join fetch p.category where p.category.categoryName =:category")
    List<Product> findProductByCategoryLowest(PageRequest pageRequest, @Param("category") String category);

    @Query("select p from Product p join fetch p.category where p.category.categoryName =:category")
    List<Product> findProductByCategorySoldCount(PageRequest pageRequest, @Param("category") String category);
}
