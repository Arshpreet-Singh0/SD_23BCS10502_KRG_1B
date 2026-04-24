package com.ecommerce.search_service.controllers;

import com.ecommerce.search_service.documents.ProductSearchDocument;
import com.ecommerce.search_service.services.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/products")
    public List<ProductSearchDocument> searchProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return searchService.searchProducts(q, minPrice, maxPrice, page, size);
    }

}
