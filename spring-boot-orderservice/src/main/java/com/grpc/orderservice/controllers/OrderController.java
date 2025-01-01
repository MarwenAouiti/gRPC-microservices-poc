package com.grpc.orderservice.controllers;




import com.grpc.orderservice.dto.OrderRequest;
import orderinventory.InventoryServiceGrpc;
import orderinventory.OrderInventory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private static final Logger logger = Logger.getLogger(OrderController.class.getName());
    private final InventoryServiceGrpc.InventoryServiceBlockingStub inventoryStub;

    public OrderController(InventoryServiceGrpc.InventoryServiceBlockingStub inventoryStub) {
        this.inventoryStub = inventoryStub;
    }

    // Example JSON body: { "orderId": "123", "productId": "ABC", "quantity": 2 }
    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request) {
        logger.info("Received order request for order " + request.getOrderId());
        // Build gRPC request
        OrderInventory.InventoryRequest inventoryRequest = OrderInventory.InventoryRequest.newBuilder()
                .setOrderId(request.getOrderId())
                .setProductId(request.getProductId())
                .setQuantity(request.getQuantity())
                .build();

        // Send gRPC request
        OrderInventory.InventoryResponse response = inventoryStub.checkInventory(inventoryRequest);

        // Use the response to decide success/failure
        if (response.getSuccess()) {
            // Further business logic: e.g., mark order as confirmed, etc.
            return ResponseEntity.ok(response.getMessage());
        } else {
            // Handle insufficient inventory
            return ResponseEntity.badRequest().body(response.getMessage());
        }
    }
}

