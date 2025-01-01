use tonic::{transport::Server, Request, Response, Status};


pub mod orderinventory {
    tonic::include_proto!("orderinventory");
}

use orderinventory::{
    inventory_service_server::{InventoryService, InventoryServiceServer},
    InventoryRequest, InventoryResponse,
};

#[derive(Debug, Default)]
pub struct MyInventoryService;

#[tonic::async_trait]
impl InventoryService for MyInventoryService {
    async fn check_inventory(
        &self,
        request: Request<InventoryRequest>,
    ) -> Result<Response<InventoryResponse>, Status> {
        let req = request.into_inner();
        println!("Received order {}, product {}, quantity {}",
            req.order_id, req.product_id, req.quantity
        );

        let in_stock = 10; 

        let resp = if req.quantity <= in_stock {
            InventoryResponse {
                success: true,
                message: format!(
                    "Item {} is available in quantity {} for order {}",
                    req.product_id, req.quantity, req.order_id
                )
            }
        } else {
            InventoryResponse {
                success: false,
                message: format!(
                    "Not enough inventory for product {} (Requested {}, have {})",
                    req.product_id, req.quantity, in_stock
                )
            }
        };

        Ok(Response::new(resp))
    }
}

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    println!("Starting Rust Inventory Service on [::1]:50051");

    let addr = "[::1]:50051".parse()?;
    let svc = MyInventoryService::default();

    Server::builder()
        .add_service(InventoryServiceServer::new(svc))
        .serve(addr)
        .await?;

    Ok(())
}
