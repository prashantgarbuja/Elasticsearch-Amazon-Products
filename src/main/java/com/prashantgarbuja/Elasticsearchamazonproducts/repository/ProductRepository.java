package com.prashantgarbuja.Elasticsearchamazonproducts.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.prashantgarbuja.Elasticsearchamazonproducts.model.Product;

public interface ProductRepository extends ElasticsearchRepository<Product, Integer>{

	List<Product> findByProductName(String productName);
	
	List<Product>findByCategory(String category); 
	
	List<Product> findByProductNameAndCategory(String productName, String category);

}
