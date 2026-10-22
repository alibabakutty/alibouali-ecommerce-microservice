package com.alibou.customer.service;

import com.alibou.customer.dto.CustomerAuthResponse;
import com.alibou.customer.dto.CustomerLoginResponse;
import com.alibou.customer.dto.CustomerRequest;
import com.alibou.customer.entity.Customer;
import com.alibou.customer.entity.Role;
import com.alibou.customer.exception.CustomerNotFoundException;
import com.alibou.customer.mapper.CustomerMapper;
import com.alibou.customer.repository.CustomerRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public String createCustomer(@Valid CustomerRequest request) {
        var customer = mapper.toCustomer(request);
        customer.setPassword(
                passwordEncoder.encode(request.password())
        );
        customer.setRole(request.role());
        repository.save(customer);
        return customer.getId();
    }

    public CustomerLoginResponse findLoginByUsername(String username) {
        Customer customer = repository.findByUsername(username)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "No customer found with username: " + username
                ));

        return new CustomerLoginResponse(
                customer.getUsername(),
                customer.getPassword(),
                customer.getRole()
        );
    }

    public void updateCustomer(@Valid CustomerRequest request) {
        var customer = repository.findById(request.id())
                .orElseThrow(() -> new CustomerNotFoundException(
                        String.format("Cannot update customer:: No customer found with the provided ID:: %s", request.id())
                ));
        mergeCustomer(customer, request);
        repository.save(customer);
    }

    private void mergeCustomer(Customer customer, @Valid CustomerRequest request) {
        if (StringUtils.isNotBlank(request.firstName())) {
            customer.setFirstname(request.firstName());
        }
        if (StringUtils.isNotBlank(request.lastName())) {
            customer.setLastname(request.lastName());
        }
        if (StringUtils.isNotBlank(request.email())) {
            customer.setEmail(request.email());
        }
        if(request.address() != null){
            customer.setAddress(request.address());
        }
        if (StringUtils.isNotBlank(request.username())) {
            customer.setUsername(request.username());
        }
        if (StringUtils.isNotBlank(request.password())) {
            customer.setPassword(
                    passwordEncoder.encode(request.password())
            );
        }
        if (request.role() != null) {
            customer.setRole(request.role());
        }
    }

    public List<CustomerAuthResponse> findAllCustomers() {
        return repository.findAll()
                .stream()
                .map(mapper::fromCustomer)
                .collect(Collectors.toList());
    }

    public Boolean existsById(String customerId) {
        return repository.findById(customerId).isPresent();
    }

    public CustomerAuthResponse findById(String customerId) {
        return repository.findById(customerId)
                .map(mapper::fromCustomer)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("No customer found with the provided ID:: %s", customerId)));
    }

    public void deleteCustomer(String customerId) {
        repository.deleteById(customerId);
    }

    public CustomerAuthResponse findByUsername(String username) {
        Customer customer = repository.findByUsername(username)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "No customer found with username: " + username
                ));

        return mapper.fromCustomer(customer);
    }
}
