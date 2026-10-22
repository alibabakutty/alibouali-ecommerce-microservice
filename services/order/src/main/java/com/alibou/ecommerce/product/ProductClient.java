package com.alibou.ecommerce.product;

import com.alibou.ecommerce.dto.PurchaseRequest;
import com.alibou.ecommerce.dto.PurchaseResponse;
import com.alibou.ecommerce.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
public class ProductClient {

    private final RestTemplate restTemplate;

    public List<PurchaseResponse> purchaseProducts(
            List<PurchaseRequest> requestBody
    ) {

        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        HttpEntity<List<PurchaseRequest>> requestEntity =
                new HttpEntity<>(requestBody, headers);

        ParameterizedTypeReference<List<PurchaseResponse>> responseType =
                new ParameterizedTypeReference<>() {};

        ResponseEntity<List<PurchaseResponse>> responseEntity =
                restTemplate.exchange(
                        "http://product-service/api/v1/products/purchase",
                        POST,
                        requestEntity,
                        responseType
                );

        if (responseEntity.getStatusCode().isError()) {
            throw new BusinessException(
                    "An error occurred while processing the products purchase: "
                            + responseEntity.getStatusCode()
            );
        }

        return responseEntity.getBody();
    }
}