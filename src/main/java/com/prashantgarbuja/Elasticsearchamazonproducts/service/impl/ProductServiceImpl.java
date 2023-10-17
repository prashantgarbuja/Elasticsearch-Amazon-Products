package com.prashantgarbuja.Elasticsearchamazonproducts.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import com.prashantgarbuja.Elasticsearchamazonproducts.model.Product;
import com.prashantgarbuja.Elasticsearchamazonproducts.repository.ProductRepository;
import com.prashantgarbuja.Elasticsearchamazonproducts.service.ProductService;
import com.prashantgarbuja.Elasticsearchamazonproducts.service.exception.DuplicateProductIdException;
import com.prashantgarbuja.Elasticsearchamazonproducts.service.exception.ProductNotFoundException;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	
	private final ElasticsearchTemplate elasticsearchTemplate;
	
	
	public ProductServiceImpl(ProductRepository productRepository, ElasticsearchTemplate elasticsearchTemplate) {
		this.productRepository = productRepository;
		this.elasticsearchTemplate = elasticsearchTemplate;
	}

	@Override
	public List<Product> getAll() {
		List<Product> products = new ArrayList<>();
		productRepository.findAll().forEach(products::add);		
		return products;
	}

	@Override
	public Optional<Product> getById(int productId) {
		return productRepository.findById(productId);
	}

	@Override
	public List<Product> getByProductName(String productName) {
		return productRepository.findByProductName(productName);
	}

	@Override
	public List<Product> getByCategory(String category) {
		return productRepository.findByCategory(category);
	}

	
	@Override
	public List<Product> getByProductNameAndCategory(String productName, String category) {
		return productRepository.findByProductNameAndCategory(productName, category);
//		var criteria = QueryBuilders.bool(builder -> builder.must(
//	            match(queryProductName -> queryProductName.field("productName").query(productName)),
//	            match(queryCategory -> queryCategory.field("category").query(category))
//	        ));
//
//	        return elasticsearchTemplate.search(NativeQuery.builder().withQuery(criteria).build(), Product.class)
//	            .stream().map(SearchHit::getContent).toList();
	}
	
	@Override
	public Product create(Product product) throws DuplicateProductIdException {
		if (getById(product.getProductId()).isEmpty()) {
			return productRepository.save(product);
		}
		throw new DuplicateProductIdException(String.format("The provided Product ID - %s already exists.", product.getProductId()));
	}

	@Override
	public void deleteByProductId(int productId) {
		productRepository.deleteById(productId);
		
	}

	@Override
	public Product update(int productId, Product product) throws ProductNotFoundException {
		Product oldProduct = productRepository
				.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found."));
		oldProduct.setCategory(product.getCategory());
		oldProduct.setProductName(product.getProductName());
		oldProduct.setDescription(product.getDescription());
		oldProduct.setPrice(product.getPrice());
		return productRepository.save(oldProduct);
	}

}
