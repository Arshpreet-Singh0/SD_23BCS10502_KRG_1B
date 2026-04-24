package com.ecommerce.order_service.clients;

import com.ecommerce.order_service.advices.ApiResponse;
import com.ecommerce.order_service.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service", path = "/product")
public interface ProductFeignClient {

    @PostMapping("/get")
    ApiResponse<List<ProductDTO>> getProductsByIds(@RequestBody List<Long> ids);

}
