package com.app.productservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.productservice.model.Product;
import com.app.productservice.repository.ProductRepository;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public Product findById(Long id) {
		return productRepository.findById(id).orElseThrow();
	}

	public List<Product> findAll() {
		return productRepository.findAll();
	}

	public Product save(Product product) {
		return productRepository.save(product);
	}

	public Product update(Long id, Product product) {
		Product existingProduct = productRepository.findById(id).orElseThrow();

		existingProduct.setName(product.getName());
		existingProduct.setDescription(product.getDescription());
		existingProduct.setPrice(product.getPrice());
		existingProduct.setStock(product.getStock());

		return productRepository.save(existingProduct);
	}

	public void deleteById(Long id) {
		productRepository.deleteById(id);
	}
	
	public Product updateStock(Long id, Integer quantity) {

	    Product product = findById(id);

	    if (product == null) {
	        return null;
	    }

	    if (product.getStock() < quantity) {
	        throw new IllegalArgumentException(
	                "Stock insuficiente para el producto: " + id);
	    }

	    product.setStock(product.getStock() - quantity);

	    return productRepository.save(product);
	}
	
	public Product restoreStock(Long id, Integer quantity) {
	    Product product = findById(id);

	    if (product == null) {
	        return null;
	    }

	    product.setStock(product.getStock() + quantity);

	    return productRepository.save(product);
	}
}
