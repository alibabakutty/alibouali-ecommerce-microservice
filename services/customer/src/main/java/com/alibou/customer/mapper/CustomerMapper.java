package com.alibou.customer.mapper;

import com.alibou.customer.dto.CustomerAuthResponse;
import com.alibou.customer.dto.CustomerRequest;
import com.alibou.customer.entity.Customer;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class CustomerMapper {
    public Customer toCustomer(@Valid CustomerRequest request) {
        if(request == null){
            return null;
        }

        return Customer.builder()
                .id(request.id())
                .firstname(request.firstName())
                .lastname(request.lastName())
                .email(request.email())
                .address(request.address())
                .role(request.role())
                .username(request.username())
                .password(request.password())
                .build();
    }

    public CustomerAuthResponse fromCustomer(Customer customer) {
        if (customer == null) {
            return null;
        }
        return new CustomerAuthResponse(
                customer.getId(),
                customer.getFirstname(),
                customer.getLastname(),
                customer.getEmail(),
                customer.getAddress(),
                customer.getRole(),
                customer.getUsername()
        );
    }
}
