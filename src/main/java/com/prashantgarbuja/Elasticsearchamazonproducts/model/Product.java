package com.prashantgarbuja.Elasticsearchamazonproducts.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Document(indexName = "products")
public class Product {
	
	@Id
	private int productId;
	
	private String productName;
	
	private Double price;
	
	private String category;
	
	private String description;

}
