# Spring Boot Order Service - README

## Overview

This Java 21 + Spring Boot service receives order requests (HTTP/JSON) on `localhost:8080` (by default). It then creates a gRPC client, sends the order details to the Rust Inventory Service, and returns a success/failure message to the caller.

## Prerequisites

- Java 21 (configured via Gradle toolchains)
- Gradle
- Protobuf compiler (protoc) installed

## Project Structure

```bash
spring-boot-orderservice/
├── build.gradle
├── settings.gradle
├── src
│   ├── main
│   │   ├── java/com/grpc/orderservice
│   │   │   ├── GRPCClientConfig.java
│   │   │   ├── OrderController.java
│   │   │   ├── OrderRequest.java
│   │   │   └── OrderServiceApplication.java
│   │   └── proto/order_inventory.proto
│   └── test
└── ...
```

- `build.gradle`: Configures Spring Boot, gRPC libraries (`grpc-netty`, `grpc-stub`, `grpc-protobuf`), and the protobuf Gradle plugin.
- `src/main/proto/order_inventory.proto`: Same `.proto` as the Rust service (kept in sync).
- `GRPCClientConfig.java`: Sets up a `ManagedChannel` and `InventoryServiceGrpc.InventoryServiceBlockingStub`.
- `OrderController.java`: Exposes a `/orders` HTTP endpoint; calls the gRPC stub.

## How to Build and Run

Clone or navigate to the project:

```bash
git clone https://github.com/YourUser/spring-boot-orderservice.git
cd spring-boot-orderservice
```

Build with Gradle:

```bash
./gradlew clean build
```

This triggers the protobuf Gradle plugin to generate Java classes for the gRPC stubs.

Run the application:

```bash
./gradlew bootRun
```

The service starts on `localhost:8080`.

## Usage

You can send a JSON POST request to `/orders`, for example:

```bash
curl -X POST -H "Content-Type: application/json" \
    -d '{"orderId":"123","productId":"ABC","quantity":2}' \
    http://localhost:8080/orders
```

The `OrderController` constructs a gRPC `InventoryRequest` and invokes `inventoryServiceBlockingStub.checkInventory(...)`.
The Rust Inventory Service receives the gRPC request, checks stock, and replies.
The Order Service then returns HTTP 200 (success) or 400 (failure) with a message.

Example success response:

```text
Product ABC is available in quantity 2 for order 123
```

Example failure response:

```text
Not enough inventory for product ABC (Requested 11, have 10)
```

Check the logs on both services:

- Order Service log should show `Sending gRPC request to Inventory Service`.
- Inventory Service log should show `Received order: ....`.

## End-to-End Testing

1. Run the Rust Inventory Service (`cargo run`) on `localhost:50051`.
2. Run this Spring Boot service (`./gradlew bootRun`) on `localhost:8080`.
3. Send an order via cURL or Postman as shown above.
4. Observe the success/failure message.
