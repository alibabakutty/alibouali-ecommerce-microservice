package com.alibou.customer.mapper;

import com.alibou.customer.dto.CustomerAuthResponse;
import com.alibou.customer.dto.CustomerRequest;
import com.alibou.customer.entity.Address;
import com.alibou.customer.entity.Customer;
import com.alibou.customer.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerMapperTest {

    private CustomerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CustomerMapper();
    }

    @Test
    void toCustomer_shouldMapAllFields() {
        Address address = Address.builder()
                .street("Main Street")
                .houseNumber("12")
                .zipCode("600001")
                .build();

        CustomerRequest request = new CustomerRequest(
                "customer-1",
                "Ada",
                "Lovelace",
                "ada@example.com",
                address,
                Role.USER,
                "ada",
                "plain-password"
        );

        Customer customer = mapper.toCustomer(request);

        assertNotNull(customer);
        assertEquals("customer-1", customer.getId());
        assertEquals("Ada", customer.getFirstname());
        assertEquals("Lovelace", customer.getLastname());
        assertEquals("ada@example.com", customer.getEmail());
        assertEquals(address, customer.getAddress());
        assertEquals(Role.USER, customer.getRole());
        assertEquals("ada", customer.getUsername());
        assertEquals("plain-password", customer.getPassword());
    }

    @Test
    void toCustomer_shouldReturnNull_whenRequestIsNull() {
        assertNull(mapper.toCustomer(null));
    }

    @Test
    void fromCustomer_shouldMapFieldsWithoutExposingPassword() {
        Address address = Address.builder()
                .street("Main Street")
                .houseNumber("12")
                .zipCode("600001")
                .build();

        Customer customer = Customer.builder()
                .id("customer-1")
                .firstname("Ada")
                .lastname("Lovelace")
                .email("ada@example.com")
                .address(address)
                .role(Role.USER)
                .username("ada")
                .password("encoded-secret")
                .build();

        CustomerAuthResponse response = mapper.fromCustomer(customer);

        assertNotNull(response);
        assertEquals("customer-1", response.id());
        assertEquals("Ada", response.firstName());
        assertEquals("Lovelace", response.lastName());
        assertEquals("ada@example.com", response.email());
        assertEquals(address, response.address());
        assertEquals(Role.USER, response.role());
        assertEquals("ada", response.username());
    }

    @Test
    void fromCustomer_shouldReturnNull_whenCustomerIsNull() {
        assertNull(mapper.fromCustomer(null));
    }
}
