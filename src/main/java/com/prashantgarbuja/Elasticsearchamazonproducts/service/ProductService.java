package com.prashantgarbuja.Elasticsearchamazonproducts.service;

import java.util.List;
import java.util.Optional;

import com.prashantgarbuja.Elasticsearchamazonproducts.model.Product;
import com.prashantgarbuja.Elasticsearchamazonproducts.service.exception.DuplicateProductIdException;
import com.prashantgarbuja.Elasticsearchamazonproducts.service.exception.ProductNotFoundException;

public interface ProductService {
	
	List<Product> getAll();
	
	Optional<Product>getById(int productId);
	
	List<Product> getByProductName(String productName);
	
	List<Product> getByCategory(String category);

	List<Product> getByProductNameAndCategory(String productName, String category);

	Product create(Product product) throws DuplicateProductIdException;
	
	void deleteByProductId(int productId);
	
	Product update(int productId, Product product) throws ProductNotFoundException;

}
