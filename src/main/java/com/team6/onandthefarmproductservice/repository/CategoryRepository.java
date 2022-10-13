package com.team6.onandthefarmproductservice.repository;

import org.springframework.data.repository.CrudRepository;

import com.team6.onandthefarmproductservice.entity.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {
}
