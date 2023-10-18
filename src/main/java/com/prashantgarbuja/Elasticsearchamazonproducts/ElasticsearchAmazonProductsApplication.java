package com.prashantgarbuja.Elasticsearchamazonproducts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Product API", 
version = "1.0", 
description = "Product API with ElasticSearch",
contact = @Contact(
        name = "Prashant Garbuja",
        email = "prashantmaha2@gmail.com",
        url = "https://prashantgarbuja.com"
),
license = @License(
        name = "MIT License",
        url = "https://github.com/prashantgarbuja/Elasticsearch-Amazon-Products/blob/main/LICENSE"
)))
public class ElasticsearchAmazonProductsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElasticsearchAmazonProductsApplication.class, args);
	}

}
