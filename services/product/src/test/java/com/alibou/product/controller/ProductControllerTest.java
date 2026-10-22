
package com.alibou.product.controller;

import com.alibou.product.dto.ProductPurchaseRequest;
import com.alibou.product.dto.ProductPurchaseResponse;
import com.alibou.product.dto.ProductRequest;
import com.alibou.product.dto.ProductResponse;
import com.alibou.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.List;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(ProductControllerTest.TestCacheConfig.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService service;

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest request = new ProductRequest(
                null,
                "Laptop",
                "Gaming laptop",
                10.0,
                new BigDecimal("1299.99"),
                2
        );

        when(service.createProduct(any(ProductRequest.class)))
                .thenReturn(1);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));

        verify(service).createProduct(any(ProductRequest.class));
    }

    @Test
    void shouldPurchaseProducts() throws Exception {
        List<ProductPurchaseRequest> requests = List.of(
                new ProductPurchaseRequest(1, 2.0)
        );

        ProductPurchaseResponse response = new ProductPurchaseResponse(
                1,
                "Laptop",
                "Gaming laptop",
                new BigDecimal("1299.99"),
                2.0
        );

        when(service.purchaseProducts(anyList()))
                .thenReturn(List.of(response));

        mockMvc.perform(post("/api/v1/products/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].quantity").value(2.0));

        verify(service).purchaseProducts(anyList());
    }

    @Test
    void shouldFindProductById() throws Exception {
        ProductResponse response = new ProductResponse(
                1,
                "Laptop",
                "Gaming laptop",
                10.0,
                new BigDecimal("1299.99"),
                2,
                "Electronics",
                "Electronic products"
        );

        when(service.findById(1)).thenReturn(response);

        mockMvc.perform(get("/api/v1/products/{product-id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.availableQuantity").value(10.0))
                .andExpect(jsonPath("$.categoryName").value("Electronics"));

        verify(service).findById(1);
    }

    @Test
    void shouldFindAllProducts() throws Exception {
        ProductResponse first = new ProductResponse(
                1,
                "Laptop",
                "Gaming laptop",
                10.0,
                new BigDecimal("1299.99"),
                2,
                "Electronics",
                "Electronic products"
        );

        ProductResponse second = new ProductResponse(
                2,
                "Mouse",
                "Wireless mouse",
                25.0,
                new BigDecimal("29.99"),
                2,
                "Electronics",
                "Electronic products"
        );

        when(service.findAll()).thenReturn(List.of(first, second));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].name").value("Mouse"));

        verify(service).findAll();
    }

    @TestConfiguration
    static class TestCacheConfig {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager();
        }
    }
}