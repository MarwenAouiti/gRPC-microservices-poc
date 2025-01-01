# Rust Inventory Service - README

## Overview

This Rust service provides inventory checks via gRPC. It listens for requests on `localhost:50051` by default and responds with whether the requested product quantity is available.

## Prerequisites

- Rust (stable or newer; e.g., Rust 1.70+)
- Cargo (bundled with Rust)
- Protobuf compiler (`protoc`) installed on your system
- Tonic is used as the gRPC framework

## Project Structure

```plaintext
rust-inventory-service/
├── Cargo.toml
├── build.rs
├── proto
│   └── order_inventory.proto
└── src
    └── main.rs
```

- `Cargo.toml`: Lists dependencies (tonic, prost, etc.)
- `build.rs`: Invokes `tonic_build` to generate Rust code from `.proto`
- `proto/order_inventory.proto`: The gRPC contract (messages + service)
- `src/main.rs`: Implementation of the `InventoryService` logic

## How to Build and Run

### Install `protoc` (if not already):

```bash
protoc --version
```

If you see a version like `libprotoc 3.21.x`, you’re good. Otherwise, install from [Protobuf Releases](https://github.com/protocolbuffers/protobuf/releases).

### Clone or navigate to this project directory:

```bash
git clone https://github.com/YourUser/rust-inventory-service.git
cd rust-inventory-service
```

### Build the service:

```bash
cargo build
```

This triggers the `build.rs` script to generate the gRPC code from `order_inventory.proto`.

### Run the service:

```bash
cargo run
```

By default, it listens on `[::1]:50051` (i.e., `localhost:50051`).

## Usage

The Rust service exposes a single gRPC endpoint: `CheckInventory`. It receives:

- `orderId` (string)
- `productId` (string)
- `quantity` (int32)

And responds with:

- `success` (bool)
- `message` (string)

## Example Log Output

When the service receives a gRPC request, you should see something like:

```yaml
Received order: 123, product: ABC, quantity: 2
```

Then it will respond with a success/failure message depending on the hard-coded stock.

## Testing the Service

You can test from any gRPC client or via the Order Service (see the companion Spring Boot project). If you have tools like `grpcurl`, you can do:

```bash
grpcurl -plaintext -d '{"orderId":"123","productId":"ABC","quantity":2}' localhost:50051 orderinventory.InventoryService/CheckInventory
```

That will show you the JSON response. If enough stock is available, you’ll see something like:

```json
{
  "success": true,
  "message": "Product ABC is available in quantity 2 for order 123"
}
```
