
package com.team6.onandthefarmproductservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.team6.onandthefarmproductservice.entity.Product;

public interface ProductPagingRepository extends PagingAndSortingRepository<Product, Long> {

	@Query(value = "select p from Product p join fetch p.category",
			countQuery = "select count(p) from Product p")
	Page<Product> findAllProductOrderByNewest(PageRequest pageRequest);

	@Query(value = "select p from Product p join fetch p.category",
			countQuery = "select count(p) from Product p")
	Page<Product> findProductListByHighPrice(PageRequest pageRequest);

	@Query(value = "select p from Product p join fetch p.category",
			countQuery = "select count(p) from Product p")
	Page<Product> findProductListByLowPrice(PageRequest pageRequest);

	@Query(value = "select p from Product p join fetch p.category",
			countQuery = "select count(p) from Product p")
	Page<Product> findProductBySoldCount(PageRequest pageRequest);

	@Query(value = "select p from Product p join fetch p.category where p.sellerId =:sellerId and p.productStatus ='selling'",
			countQuery = "select count(p) from Product p")
	Page<Product> findProductBySellerNewest(PageRequest pageRequest, @Param("sellerId") Long sellerId);

	@Query(value = "select p from Product p join fetch p.category where p.sellerId =:sellerId and p.productStatus ='selling'",
			countQuery = "select count(p) from Product p")
	Page<Product> findSellingProductBySellerNewest(PageRequest pageRequest, @Param("sellerId") Long sellerId);

	@Query(value = "select p from Product p join fetch p.category where p.sellerId =:sellerId and p.productStatus ='pause'",
			countQuery = "select count(p) from Product p")
	Page<Product> findPauseProductBySellerNewest(PageRequest pageRequest, @Param("sellerId") Long sellerId);

	@Query(value = "select p from Product p join fetch p.category where p.category.categoryName =:category",
			countQuery = "select count(p) from Product p")
	Page<Product> findProductsByCategoryNewest(PageRequest pageRequest, @Param("category") String category);

	@Query(value = "select p from Product p join fetch p.category where p.category.categoryName =:category",
			countQuery = "select count(p) from Product p")
	Page<Product> findProductByCategoryHighest(PageRequest pageRequest, @Param("category") String category);

	@Query(value = "select p from Product p join fetch p.category where p.category.categoryName =:category",
			countQuery = "select count(p) from Product p")
	Page<Product> findProductByCategoryLowest(PageRequest pageRequest, @Param("category") String category);

	@Query(value = "select p from Product p join fetch p.category where p.category.categoryName =:category",
			countQuery = "select count(p) from Product p")
	Page<Product> findProductByCategorySoldCount(PageRequest pageRequest, @Param("category") String category);
}
