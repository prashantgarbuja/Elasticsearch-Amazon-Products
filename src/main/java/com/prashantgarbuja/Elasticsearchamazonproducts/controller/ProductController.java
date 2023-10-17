package com.prashantgarbuja.Elasticsearchamazonproducts.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prashantgarbuja.Elasticsearchamazonproducts.model.Product;
import com.prashantgarbuja.Elasticsearchamazonproducts.service.ProductService;
import com.prashantgarbuja.Elasticsearchamazonproducts.service.exception.DuplicateProductIdException;
import com.prashantgarbuja.Elasticsearchamazonproducts.service.exception.ProductNotFoundException;

@RestController
@RequestMapping("/v1/products")
public class ProductController {
	
	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}
	
	@GetMapping
	public List<Product> getAllProducts() {
		return productService.getAll();
	}

	@GetMapping(value = "/{productId}")
	public ResponseEntity<Product> getProductById(@PathVariable int productId) throws ProductNotFoundException {
		try {
			Product product = productService.getById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product Id not found."));
		return ResponseEntity.ok(product);
		} catch (ProductNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}
	
	@PostMapping
	public Product createProduct(@RequestBody Product product) throws DuplicateProductIdException {
		return productService.create(product);
		
	}
	
	@GetMapping(value = "/productName/{productName}")
	public List<Product> getByProductName(@PathVariable String productName) {
		return productService.getByProductName(productName);
	}
	
	@GetMapping(value = "/category/{category}")
	public List<Product> getByCategory(@PathVariable String category) {
		return productService.getByCategory(category);
	}
	
	@GetMapping(value = "/query")
	public List<Product> getByProductNameAndCategory(@RequestParam(value = "productName") String productName,@RequestParam(value = "category") String category) {
		return productService.getByProductNameAndCategory(productName, category);
	}
	
	@PutMapping(value = "/{productId}")
	public Product updateProduct(@PathVariable int productId, @RequestBody Product product) throws ProductNotFoundException {
		return productService.update(productId, product);
	}
	
	@DeleteMapping(value = "/{productId}")
	public void deleteProduct(@PathVariable int productId) {
		productService.deleteByProductId(productId);
	}
}
