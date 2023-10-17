package com.prashantgarbuja.Elasticsearchamazonproducts.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.prashantgarbuja.Elasticsearchamazonproducts.model.Product;
import com.prashantgarbuja.Elasticsearchamazonproducts.service.ProductService;
import com.prashantgarbuja.Elasticsearchamazonproducts.service.exception.DuplicateProductIdException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTest {

	private static final String ELASTIC_SEARCH_DOCKER = "elasticsearch:8.7.0";
    private static final String CLUSTER_NAME = "cluster.name";
    private static final String ELASTIC_SEARCH = "elasticsearch";
    private static final String DISCOVERY_TYPE = "discovery.type";
    private static final String DISCOVERY_TYPE_SINGLE_NODE = "single-node";
    private static final String XPACK_SECURITY_ENABLED = "xpack.security.enabled";

	@Container
	@ServiceConnection
	static ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer(
			DockerImageName.parse(ELASTIC_SEARCH_DOCKER)
					.asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch"))
			.withEnv(DISCOVERY_TYPE, DISCOVERY_TYPE_SINGLE_NODE)
			.withEnv(XPACK_SECURITY_ENABLED, Boolean.FALSE.toString())
			.withEnv(CLUSTER_NAME, ELASTIC_SEARCH)
			.withExposedPorts(9200);

	@Autowired
	private ProductService productService;
	@Autowired
	TestRestTemplate restTemplate;

	@Autowired
	private ElasticsearchTemplate template;

	@BeforeEach
	void testIsContainerRunning() throws DuplicateProductIdException {
		assertTrue(elasticsearchContainer.isRunning());
		recreateIndex();
		insertProducts();
	}

	private void recreateIndex() {
		if (template.indexOps(Product.class).exists()) {
			template.indexOps(Product.class).delete();
			template.indexOps(Product.class).create();
		}
	}

	@Test
	void shouldFindAllProducts() throws DuplicateProductIdException {
		// /v1/products
		Product[] products = restTemplate.getForObject("/v1/products", Product[].class);
		assertThat(products.length).isEqualTo(4);
	}

	@Test
	void shouldFindByProductID()  {
		ResponseEntity<Product> response = restTemplate.exchange("/v1/products/1", HttpMethod.GET, null, Product.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
	}

	@Test
	void shouldThrowNotFoundWithInvalidID()  {
		ResponseEntity<Product> response = restTemplate.exchange("/v1/products/5", HttpMethod.GET, null, Product.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	private void insertProducts() throws DuplicateProductIdException {
		productService.create(new Product(1, "Asus VivoBook Pro 17", 1400.00, "Electronics", "Asus Laptop - Intel Core i7 8th Gen, NVIDIA GEFORCE GTX"));
		productService.create(new Product(2, "Samsung S21", 1250.00, "Electronics", "Samsung S21 - 16GB RAM, 128GB Internal Storage"));
		productService.create(new Product(3, "Watch", 850.50, "Accessories", "Platinum Gold watch with silver lining."));
		productService.create(new Product(4, "The Alchemist", 650.25, "Books", "Top selling Books"));
	}

}
