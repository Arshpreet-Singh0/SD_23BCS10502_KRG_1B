package com.ecommerce.order_service.clients;

import com.ecommerce.order_service.dto.ReserveInventoryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "inventory-service", path = "/inventory")
public interface InventoryFeignClient {

    @PostMapping("/reserve")
    void reserve(ReserveInventoryRequest inventoryReserveDTO);

}
