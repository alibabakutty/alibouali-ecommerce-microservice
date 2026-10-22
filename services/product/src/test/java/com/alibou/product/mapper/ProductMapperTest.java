package com.alibou.product.mapper;

import com.alibou.product.dto.ProductPurchaseResponse;
import com.alibou.product.dto.ProductRequest;
import com.alibou.product.dto.ProductResponse;
import com.alibou.product.entity.Category;
import com.alibou.product.entity.Product;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    private final ProductMapper mapper = new ProductMapper();

    @Test
    void toProductShouldMapRequestFields() {
        ProductRequest request = new ProductRequest(
                1, "Laptop", "Gaming laptop",
                10.0, new BigDecimal("1299.99"), 2
        );

        Product product = mapper.toProduct(request);

        assertEquals(1, product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals("Gaming laptop", product.getDescription());
        assertEquals(10.0, product.getAvailableQuantity());
        assertEquals(new BigDecimal("1299.99"), product.getPrice());
        assertNotNull(product.getCategory());
        assertEquals(2, product.getCategory().getId());
    }

    @Test
    void toProductResponseShouldMapProductAndCategory() {
        Category category = Category.builder()
                .id(2)
                .name("Electronics")
                .description("Electronic products")
                .build();

        Product product = Product.builder()
                .id(1)
                .name("Laptop")
                .description("Gaming laptop")
                .availableQuantity(10.0)
                .price(new BigDecimal("1299.99"))
                .category(category)
                .build();

        ProductResponse response = mapper.toProductResponse(product);

        assertEquals(1, response.id());
        assertEquals("Laptop", response.name());
        assertEquals("Gaming laptop", response.description());
        assertEquals(10.0, response.availableQuantity());
        assertEquals(new BigDecimal("1299.99"), response.price());
        assertEquals(2, response.categoryId());
        assertEquals("Electronics", response.categoryName());
        assertEquals("Electronic products", response.categoryDescription());
    }

    @Test
    void toProductPurchaseResponseShouldMapProductAndRequestedQuantity() {
        Product product = Product.builder()
                .id(1)
                .name("Laptop")
                .description("Gaming laptop")
                .availableQuantity(10.0)
                .price(new BigDecimal("1299.99"))
                .build();

        ProductPurchaseResponse response =
                mapper.toProductPurchaseResponse(product, 3.0);

        assertEquals(1, response.productId());
        assertEquals("Laptop", response.name());
        assertEquals("Gaming laptop", response.description());
        assertEquals(new BigDecimal("1299.99"), response.price());
        assertEquals(3.0, response.quantity());
    }
}