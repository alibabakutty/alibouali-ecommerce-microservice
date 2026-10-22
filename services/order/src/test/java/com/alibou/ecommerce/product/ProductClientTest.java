package com.alibou.ecommerce.product;

import com.alibou.ecommerce.dto.PurchaseRequest;
import com.alibou.ecommerce.dto.PurchaseResponse;
import com.alibou.ecommerce.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProductClientTest {

    private static final String URL =
            "http://product-service/api/v1/products/purchase";

    private final RestTemplate restTemplate = mock(RestTemplate.class);
    private final ProductClient productClient = new ProductClient(restTemplate);

    @SuppressWarnings("unchecked")
    private ResponseEntity<List<PurchaseResponse>> stubExchange(
            ResponseEntity<List<PurchaseResponse>> response
    ) {
        when(restTemplate.exchange(
                eq(URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<List<PurchaseResponse>>>any()
        )).thenReturn(response);

        return response;
    }

    @Test
    void shouldReturnPurchaseResponsesWhenRequestSucceeds() {
        List<PurchaseRequest> requests = List.of(
                new PurchaseRequest(1, 2.0)
        );

        List<PurchaseResponse> expected = List.of(
                new PurchaseResponse(
                        1, "Laptop", "Business laptop",
                        new BigDecimal("1250.00"), 2.0
                )
        );

        stubExchange(ResponseEntity.ok(expected));

        List<PurchaseResponse> actual =
                productClient.purchaseProducts(requests);

        assertEquals(expected, actual);
    }

    @Test
    void shouldSendJsonRequestWithPurchaseBody() {
        List<PurchaseRequest> requests = List.of(
                new PurchaseRequest(1, 2.0)
        );

        stubExchange(ResponseEntity.ok(List.of()));

        productClient.purchaseProducts(requests);

        verify(restTemplate).exchange(
                eq(URL),
                eq(HttpMethod.POST),
                argThat((HttpEntity<?> entity) ->
                        entity != null
                                && entity.getBody().equals(requests)
                                && MediaType.APPLICATION_JSON_VALUE.equals(
                                entity.getHeaders().getFirst(
                                        HttpHeaders.CONTENT_TYPE
                                )
                        )
                ),
                ArgumentMatchers.<ParameterizedTypeReference<List<PurchaseResponse>>>any()
        );
    }

    @Test
    void shouldThrowBusinessExceptionWhenResponseIsAnError() {
        List<PurchaseRequest> requests = List.of(
                new PurchaseRequest(1, 2.0)
        );

        stubExchange(
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(List.of())
        );

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> productClient.purchaseProducts(requests)
        );

        assertTrue(exception.getMsg().contains(
                "An error occurred while processing the products purchase"
        ));
        assertTrue(exception.getMsg().contains("400 BAD_REQUEST"));
    }

    @Test
    void shouldPropagateRestTemplateException() {
        List<PurchaseRequest> requests = List.of(
                new PurchaseRequest(1, 2.0)
        );

        when(restTemplate.exchange(
                eq(URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<List<PurchaseResponse>>>any()
        )).thenThrow(new RuntimeException("Product service unavailable"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productClient.purchaseProducts(requests)
        );

        assertEquals("Product service unavailable", exception.getMessage());
    }

    @Test
    void shouldReturnNullWhenSuccessfulResponseBodyIsNull() {
        stubExchange(ResponseEntity.ok().<List<PurchaseResponse>>build());

        List<PurchaseResponse> actual =
                productClient.purchaseProducts(List.of());

        assertNull(actual);
    }
}