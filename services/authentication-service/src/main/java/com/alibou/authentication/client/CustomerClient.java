package com.alibou.authentication.client;

import com.alibou.authentication.dto.CustomerAuthResponse;
import com.alibou.authentication.dto.CustomerLoginResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerClient {

    @GetMapping("/api/v1/customers/internal/auth/{username}")
    CustomerLoginResponse getCustomerByUsername(
            @PathVariable("username") String username
    );
}
