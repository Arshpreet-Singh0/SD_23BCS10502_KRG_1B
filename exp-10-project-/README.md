# E-Commerce Microservices Architecture

This project is an E-Commerce backend built using a modern microservices architecture with Spring Boot, Spring Cloud, Kafka, and Elasticsearch. It aims to provide scalable, decoupled services with event-driven data synchronization and synchronous inter-service communication.

## Architecture Overview

The system is composed of several specialized microservices. Requests map through an API Gateway, are resolved via a Discovery Server, and rely on JWT-based authentication.

### Core Stack
- **Framework**: Spring Boot 3.x, Spring Cloud
- **Databases**: PostgreSQL (Relational), Elasticsearch (Search)
- **Message Broker**: Apache Kafka (Event-Driven Updates)
- **Security**: JWT-based Authentication
- **Service Discovery**: Netflix Eureka

---

## Microservices

### 1. **Discovery Client (Eureka Server)**
- Port: `8761`
- Acts as the service registry. All microservices register themselves here, allowing the API Gateway and Feign clients to dynamically discover service locations.

### 2. **API Gateway**
- Acts as the single entry point for the frontend/clients.
- Routes incoming requests to appropriate downstream services via `lb://SERVICE-NAME`.
- Handles prefix stripping mapping external URLs like `/api/v1/user/**` to the internal path structure.

### 3. **User Service**
- **Datastore**: PostgreSQL
- **Function**: Manages user registration, login, and authorization. 
- **Security**: Generates stateless JSON Web Tokens (JWT) inside an `HttpOnly` cookie upon successful login or refresh.

### 4. **Product Service**
- **Datastore**: Relational DB
- **Function**: Master catalog service to create and update physical entities.
- **Events**: Produces `ProductCreatedEvent` to Kafka whenever a new product is added, broadcasting availability without knowing downstream consumers.

### 5. **Inventory Service**
- **Datastore**: Relational DB
- **Function**: Tracks Sku-level stock quantities and performs reservation locking during checkout processes. Includes background schedulers for cleanup.
- **Syncing**: Consumes `ProductCreatedEvent` from Kafka to automatically prepare inventory records for newly launched products.
- **Events**: Produces `InventoryUpdatedEvent` to Kafka whenever stock numbers change.

### 6. **Search Service**
- **Datastore**: Elasticsearch
- **Function**: Optimized search engine to allow users to query products rapidly by name, filters, and prices without hitting transactional databases.
- **Syncing**: Event-Driven. It listens to Kafka for both `ProductCreatedEvent` and `InventoryUpdatedEvent` to ensure its Elasticsearch indexes are continuously synchronized with the source-of-truth services.

### 7. **Order Service**
- **Datastore**: PostgreSQL
- **Function**: Coordinates system processes to accept user orders, acting as the transactional orchestrator. 
- **Communication Flow**: 
  1. Validates the product synchronously via a **FeignClient** call to the Product Service.
  2. Reserves the stock synchronously via a **FeignClient** call to the Inventory Service.
  3. Secures endpoints with an internal JWT Auth Filter.

---

## API Endpoints (Gateway Routes)

All routes below sit behind the API Gateway prefix: `http://<gateway_host>/api/v1/`

### User Service (`/user/**`)
- `POST /user/signup` - Register a new user.
- `POST /user/login` - Authenticate and receive `refreshToken` via HttpOnly Cookie + Access Token.
- `POST /user/refresh` - Generate a new access token using a valid refresh token.

### Product Service (`/product/**`)
- `POST /product` - Create a new product. (Emits Kafka Event)
- `PUT /product` - Update existing product details.
- `POST /product/get` - Fetch a batch list of products by IDs.

### Inventory Service (`/inventory/**`)
- `GET /inventory/{sku}` - Check specific inventory limits.
- `PUT /inventory/update` - Directly update stock count.
- `POST /inventory/reserve` - Lock/Reserve stock quantity (Typically called internally by Order Service).

### Search Service (`/search/**`)
- `GET /search/products` - Search indexed store. 
  - *Query Params*: `q` (text search), `minPrice`, `maxPrice`, `page`, `size`

### Order Service (`/order/**`)
- `POST /orders` - Create an order (Requires JWT, coordinates product check & inventory reservation).

---

## System Flow Summary

1. **Write Path**: Products created in **Product Service** → Publish Event → Consumed by **Inventory Service** (prep stock) & **Search Service** (update index).
2. **Read Path**: Clients hit **Search Service** (Elasticsearch) for extreme high-speed reads and filtering.
3. **Transaction Path**: Clients place orders on **Order Service**. Order Service verifies via HTTP (Feign) with **Product Service** and coordinates a reservation lock with **Inventory Service** before confirming the transaction in its local database.
