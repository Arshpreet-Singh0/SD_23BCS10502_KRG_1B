package com.ecommerce.search_service.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import com.ecommerce.inventory_service.event.InventoryUpdatedEvent;
import com.ecommerce.product_service.event.ProductCreatedEvent;
import com.ecommerce.search_service.documents.ProductSearchDocument;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import co.elastic.clients.json.JsonData;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ElasticsearchClient elasticsearchClient;
    private final ModelMapper modelMapper;

    public void indexProduct(ProductCreatedEvent event) {

        ProductSearchDocument document =
                modelMapper.map(event, ProductSearchDocument.class);

        // Set inStock from inventory quantity
        document.setInStock(event.getTotalQuantity() != null
                && event.getTotalQuantity() > 0);

        try {
            elasticsearchClient.index(i -> i
                    .index("products")
                    .id(document.getSku())
                    .document(document)
            );

        } catch (IOException e) {
            throw new RuntimeException("Failed to index product", e);
        }
    }

    public List<ProductSearchDocument> searchProducts(
            String keyword,
            Double minPrice,
            Double maxPrice,
            int page,
            int size
    ) {

        try {
            var response = elasticsearchClient.search(s -> s
                            .index("products")
                            .from(page * size)
                            .size(size)
                            .query(q -> q.bool(b -> {

                                // Full text search
                                if (keyword != null && !keyword.isBlank()) {
                                    b.must(m -> m.multiMatch(mm -> mm
                                            .fields("name", "description")
                                            .query(keyword)
                                    ));
                                }

                                // Price filter
                                if (minPrice != null || maxPrice != null) {
                                    b.filter(f -> f.range(r -> {
                                        r.field("price");
                                        if (minPrice != null) {
                                            r.gte(JsonData.of(minPrice));
                                        }

                                        if (maxPrice != null) {
                                            r.lte(JsonData.of(maxPrice));
                                        }
                                        return r;
                                    }));
                                }

                                return b;
                            })),
                    ProductSearchDocument.class
            );

            return response.hits().hits()
                    .stream()
                    .map(h -> h.source())
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Search failed", e);
        }
    }

    public void updateInventory(InventoryUpdatedEvent event) {

        boolean inStock =
                event.getQuantity() != null && event.getQuantity() > 0;

        try {
            UpdateResponse<Map> response =
                    elasticsearchClient.update(u -> u
                                    .index("products")
                                    .id(event.getSku())
                                    .doc(Map.of("inStock", inStock)),
                            Map.class
                    );

        } catch (Exception e) {
            throw new RuntimeException("Failed to update inventory", e);
        }
    }
}