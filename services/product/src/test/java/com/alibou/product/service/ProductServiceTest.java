package com.alibou.product.service;

import com.alibou.product.dto.ProductPurchaseRequest;
import com.alibou.product.dto.ProductPurchaseResponse;
import com.alibou.product.dto.ProductRequest;
import com.alibou.product.dto.ProductResponse;
import com.alibou.product.entity.Product;
import com.alibou.product.exception.ProductPurchaseException;
import com.alibou.product.mapper.ProductMapper;
import com.alibou.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = mock(Product.class);
    }

    // --------------------------------------------------
    // CREATE PRODUCT TESTS
    // --------------------------------------------------

    @Test
    void createProduct_ShouldSaveProductAndReturnId() {
        ProductRequest request = mock(ProductRequest.class);

        when(mapper.toProduct(request)).thenReturn(product);
        when(repository.save(product)).thenReturn(product);
        when(product.getId()).thenReturn(1);

        Integer result = productService.createProduct(request);

        assertEquals(1, result);

        verify(mapper).toProduct(request);
        verify(repository).save(product);
    }

    // --------------------------------------------------
    // PURCHASE PRODUCTS TESTS
    // --------------------------------------------------

    @Test
    void purchaseProducts_ShouldReduceStockAndReturnPurchaseResponse() {
        ProductPurchaseRequest request = mock(ProductPurchaseRequest.class);
        ProductPurchaseResponse response =
                mock(ProductPurchaseResponse.class);

        when(request.productId()).thenReturn(1);
        when(request.quantity()).thenReturn(2.0);

        when(product.getAvailableQuantity()).thenReturn(10.0);

        when(repository.findByIdInOrderById(List.of(1)))
                .thenReturn(List.of(product));

        when(repository.save(product)).thenReturn(product);

        when(mapper.toProductPurchaseResponse(product, 2.0))
                .thenReturn(response);

        List<ProductPurchaseResponse> result =
                productService.purchaseProducts(List.of(request));

        assertEquals(1, result.size());
        assertSame(response, result.get(0));

        verify(product).setAvailableQuantity(8.0);
        verify(repository).save(product);
        verify(mapper).toProductPurchaseResponse(product, 2.0);
    }

    @Test
    void purchaseProducts_ShouldThrowExceptionWhenProductDoesNotExist() {
        ProductPurchaseRequest request = mock(ProductPurchaseRequest.class);

        when(request.productId()).thenReturn(99);

        when(repository.findByIdInOrderById(List.of(99)))
                .thenReturn(List.of());

        assertThrows(
                ProductPurchaseException.class,
                () -> productService.purchaseProducts(List.of(request))
        );

        verify(repository, never()).save(any(Product.class));
        verify(mapper, never())
                .toProductPurchaseResponse(any(Product.class), anyDouble());
    }

    @Test
    void purchaseProducts_ShouldThrowExceptionWhenStockIsInsufficient() {
        ProductPurchaseRequest request = mock(ProductPurchaseRequest.class);

        when(request.productId()).thenReturn(1);
        when(request.quantity()).thenReturn(15.0);

        when(repository.findByIdInOrderById(List.of(1)))
                .thenReturn(List.of(product));

        when(product.getAvailableQuantity()).thenReturn(10.0);

        assertThrows(
                ProductPurchaseException.class,
                () -> productService.purchaseProducts(List.of(request))
        );

        verify(product, never()).setAvailableQuantity(anyDouble());
        verify(repository, never()).save(any(Product.class));
    }

    // --------------------------------------------------
    // FIND PRODUCT BY ID TESTS
    // --------------------------------------------------

    @Test
    void findById_ShouldReturnProductWhenItExists() {
        ProductResponse response = mock(ProductResponse.class);

        when(repository.findById(1)).thenReturn(Optional.of(product));
        when(mapper.toProductResponse(product)).thenReturn(response);

        ProductResponse result = productService.findById(1);

        assertSame(response, result);

        verify(repository).findById(1);
        verify(mapper).toProductResponse(product);
    }

    @Test
    void findById_ShouldThrowExceptionWhenProductDoesNotExist() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> productService.findById(99)
        );

        verify(repository).findById(99);
        verify(mapper, never()).toProductResponse(any(Product.class));
    }

    // --------------------------------------------------
    // FIND ALL PRODUCTS TESTS
    // --------------------------------------------------

    @Test
    void findAll_ShouldReturnMappedProducts() {
        Product secondProduct = mock(Product.class);

        ProductResponse firstResponse = mock(ProductResponse.class);
        ProductResponse secondResponse = mock(ProductResponse.class);

        when(repository.findAll())
                .thenReturn(List.of(product, secondProduct));

        when(mapper.toProductResponse(product))
                .thenReturn(firstResponse);

        when(mapper.toProductResponse(secondProduct))
                .thenReturn(secondResponse);

        List<ProductResponse> result = productService.findAll();

        assertEquals(2, result.size());
        assertSame(firstResponse, result.get(0));
        assertSame(secondResponse, result.get(1));

        verify(repository).findAll();
        verify(mapper).toProductResponse(product);
        verify(mapper).toProductResponse(secondProduct);
    }

    @Test
    void findAll_ShouldReturnEmptyListWhenNoProductsExist() {
        when(repository.findAll()).thenReturn(List.of());

        List<ProductResponse> result = productService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository).findAll();
        verifyNoInteractions(mapper);
    }
}