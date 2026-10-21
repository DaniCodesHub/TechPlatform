package com.app.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.productservice.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
