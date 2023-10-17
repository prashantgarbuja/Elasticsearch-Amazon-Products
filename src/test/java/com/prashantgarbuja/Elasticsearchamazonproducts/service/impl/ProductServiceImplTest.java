package com.prashantgarbuja.Elasticsearchamazonproducts.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import com.prashantgarbuja.Elasticsearchamazonproducts.service.exception.ProductNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.prashantgarbuja.Elasticsearchamazonproducts.model.Product;
import com.prashantgarbuja.Elasticsearchamazonproducts.service.ProductService;
import com.prashantgarbuja.Elasticsearchamazonproducts.service.exception.DuplicateProductIdException;

@Testcontainers
//@DataElasticsearchTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceImplTest {

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
	
    @Test
    void connectionEstablished() {
    	assertThat(elasticsearchContainer.isCreated()).isTrue();
    	assertThat(elasticsearchContainer.isRunning()).isTrue();
    }
    
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ElasticsearchTemplate template;
	
	
	@BeforeAll
	static void setUp() {
		elasticsearchContainer.start();
	}
	
	@BeforeEach
	void testIsContainerRunning() {
		assertTrue(elasticsearchContainer.isRunning());
		recreateIndex();
	}

	@Test
	void testGetAll() throws DuplicateProductIdException {
		insertProducts();
		List<Product> products = productService.getAll();
		
		assertNotNull(products);
		assertEquals(5, products.size());
	}

	@Test
	void testGetById() throws DuplicateProductIdException {
		insertProducts();
		Optional<Product> product = productService.getById(1);
		assertTrue(product.isPresent());
		Product existingProduct = product.get();
		assertNotNull(existingProduct);
		assertEquals("Asus VivoBook Pro 17", existingProduct.getProductName());
		assertEquals(1400.00, existingProduct.getPrice(), 0.0);
		assertEquals("Electronics", existingProduct.getCategory());
		assertEquals("Asus Laptop - Intel Core i7 8th Gen, NVIDIA GEFORCE GTX", existingProduct.getDescription());
	}

	@Test
	void testGetByProductNameAndCategory() throws DuplicateProductIdException {
		insertProducts();
		List<Product> products = productService.getByProductNameAndCategory("Samsung S21", "Electronics");
		assertNotNull(products);
		assertEquals(2, products.size());

	}

	@Test
	void testCreateProductWithDuplicateIDThrowsException() throws DuplicateProductIdException {
		insertProducts();
		assertThrows(DuplicateProductIdException.class, () -> {
			productService.create(new Product(1, "Dummy Product", 100.00, "Dummy Category", "Dummy Description"));
		});
	}

	@Test
	void testUpdateProductWithInvalidIDThrowsException() throws DuplicateProductIdException {
		insertProducts();
		Optional<Product> product = productService.getById(1);
		assertTrue(product.isPresent());
		assertThrows(ProductNotFoundException.class, () -> {
			productService.update(6, product.get());
		});
	}

	@Test
	void testDeleteByProductId() throws DuplicateProductIdException {
		insertProducts();
		Optional<Product> product = productService.getById(1);
		assertTrue(product.isPresent());
		productService.deleteByProductId(product.get().getProductId());
		List<Product> products = productService.getByProductName("Asus VivoBook Pro 17");
		assertTrue(products.isEmpty());
	}

	@Test
	void testUpdate() throws DuplicateProductIdException, ProductNotFoundException {
		insertProducts();
		Optional<Product> product = productService.getById(1);
		assertTrue(product.isPresent());
		product.get().setCategory("Laptop");
		Product updatedProduct = productService.update(product.get().getProductId(), product.get());

		assertNotNull(updatedProduct);
		assertEquals(1,updatedProduct.getProductId());
		assertEquals("Asus VivoBook Pro 17",updatedProduct.getProductName());
		assertEquals("Laptop",updatedProduct.getCategory());
		assertEquals(1400.00,updatedProduct.getPrice());
		assertEquals("Asus Laptop - Intel Core i7 8th Gen, NVIDIA GEFORCE GTX", updatedProduct.getDescription());
	}
	
	private void insertProducts() throws DuplicateProductIdException {
		productService.create(new Product(1, "Asus VivoBook Pro 17", 1400.00, "Electronics", "Asus Laptop - Intel Core i7 8th Gen, NVIDIA GEFORCE GTX"));
		productService.create(new Product(2, "Samsung S21", 1250.00, "Electronics", "Samsung S21 - 16GB RAM, 128GB Internal Storage"));
		productService.create(new Product(5, "Samsung S21", 1350.00, "Electronics", "Samsung S21 - 16GB RAM, 128GB Internal Storage"));
		productService.create(new Product(3, "Watch", 850.50, "Accessories", "Platinum Gold watch with silver lining."));
		productService.create(new Product(4, "The Alchemist", 650.25, "Books", "Top selling Books"));
	}

	private void recreateIndex() {
		if (template.indexOps(Product.class).exists()) {
			template.indexOps(Product.class).delete();
			template.indexOps(Product.class).create();
		}
		
	}
	
	@AfterAll
	static void destroy() {
		elasticsearchContainer.stop();
	}

}
