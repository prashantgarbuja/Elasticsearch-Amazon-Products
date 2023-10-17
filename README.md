# Amazon Products with Elasticsearch Example

This example demonstrates how to build a Spring Boot application that uses Spring Data Elasticsearch to perform CRUD operations on Amazon products data. 

## Operations Supported

The application provides the following operations for managing Amazon products data:

1. Get the list of all products
2. Create a new product
3. Update an existing product by ID
4. Delete a product by ID
5. Search for a product by name and/or category

## How to Run

To get started, you'll need to have Elasticsearch up and running. You can use the provided docker-compose file to start Elasticsearch with a single-node cluster named `elasticsearch`. Use the following command:

```bash
$ docker-compose up -d
```

Next, start the Spring Boot application by running:

```bash
$ ./mvnw spring-boot:run
```

If your Elasticsearch URI is different from localhost, you can override it by setting the `ES_URI` environment variable.

Once the application is up and running, open your browser and go to [http://localhost:8080](http://localhost:8080) to interact with the API using Swagger.

## Running Testcontainers Tests

The integration tests in this project use Testcontainers to dynamically spin up an Elasticsearch instance for testing purposes. This ensures that the tests are performed against a real Elasticsearch instance.

To run the integration tests (using Testcontainers), execute the following command:

```bash
$ ./mvnw clean verify
```

Please make sure that Docker is running on your system.
