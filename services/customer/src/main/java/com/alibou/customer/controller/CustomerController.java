package com.alibou.customer.controller;

import com.alibou.customer.dto.CustomerAuthResponse;
import com.alibou.customer.dto.CustomerLoginResponse;
import com.alibou.customer.dto.CustomerRequest;
import com.alibou.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<String> createCustomer(@RequestBody @Valid CustomerRequest request){
        return ResponseEntity.ok(customerService.createCustomer(request));
    }


    @GetMapping("/internal/auth/{username}")
    public ResponseEntity<CustomerLoginResponse> getCustomerForAuthentication(
            @PathVariable String username) {
        return ResponseEntity.ok(
                customerService.findLoginByUsername(username)
        );
    }

    @PutMapping
    public ResponseEntity<Void> updateCustomer(@RequestBody @Valid CustomerRequest request){
        customerService.updateCustomer(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<CustomerAuthResponse>> findAll(){
        return ResponseEntity.ok(customerService.findAllCustomers());
    }

    @GetMapping("/exists/{customer-id}")
    public ResponseEntity<Boolean> existsById(@PathVariable("customer-id") String customerId){
        return ResponseEntity.ok(customerService.existsById(customerId));
    }


    @GetMapping("/{customer-id}")
    public ResponseEntity<CustomerAuthResponse> findById(@PathVariable("customer-id") String customerId){
        return ResponseEntity.ok(customerService.findById(customerId));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<CustomerAuthResponse> getCustomerByUsername(
            @PathVariable String username) {
        return ResponseEntity.ok(customerService.findByUsername(username));
    }

    @DeleteMapping("/{customer-id}")
    public ResponseEntity<Void> delete(@PathVariable("customer-id") String customerId){
        customerService.deleteCustomer(customerId);
        return ResponseEntity.accepted().build();
    }
}
