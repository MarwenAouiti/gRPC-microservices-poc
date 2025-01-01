package com.grpc.orderservice.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GRPCClientConfig {

    @Bean
    public ManagedChannel managedChannel() {
        // Connect to Rust service at localhost:50051
        return ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext() // disable TLS for local dev
                .build();
    }

    @Bean
    public orderinventory.InventoryServiceGrpc.InventoryServiceBlockingStub inventoryServiceBlockingStub(ManagedChannel channel) {
        return orderinventory.InventoryServiceGrpc.newBlockingStub(channel);
    }
}

