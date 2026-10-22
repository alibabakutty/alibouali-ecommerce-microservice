
package com.alibou.customer.controller;

import com.alibou.customer.dto.CustomerAuthResponse;
import com.alibou.customer.dto.CustomerRequest;
import com.alibou.customer.entity.Address;
import com.alibou.customer.entity.Customer;
import com.alibou.customer.entity.Role;
import com.alibou.customer.handler.GlobalExceptionHandler;
import com.alibou.customer.repository.CustomerRepository;
import com.alibou.customer.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import com.alibou.customer.dto.CustomerLoginResponse;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@Import(GlobalExceptionHandler.class)
class CustomerControllerTest {

    private static final String BASE_URL = "/api/v1/customers";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private CustomerRepository customerRepository;

    private CustomerRequest validRequest() {
        return new CustomerRequest(
                "customer-1",
                "Ada",
                "Lovelace",
                "ada@example.com",
                Address.builder()
                        .street("Main Street")
                        .houseNumber("12")
                        .zipCode("600001")
                        .build(),
                Role.USER,
                "ada",
                "encoded-password"
        );
    }

    private CustomerAuthResponse response() {
        CustomerRequest request = validRequest();

        return new CustomerAuthResponse(
                request.id(),
                request.firstName(),
                request.lastName(),
                request.email(),
                request.address(),
                request.role(),
                request.username()
        );
    }

    private Customer customer() {
        return Customer.builder()
                .id("customer-1")
                .firstname("Ada")
                .lastname("Lovelace")
                .email("ada@example.com")
                .address(validRequest().address())
                .role(Role.USER)
                .username("ada")
                .password("encoded-password")
                .build();
    }

    @Test
    void createCustomerReturnsCreatedCustomerMessage() throws Exception {
        when(customerService.createCustomer(any(CustomerRequest.class)))
                .thenReturn("Customer created successfully");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer created successfully"));

        verify(customerService).createCustomer(any(CustomerRequest.class));
    }

    @Test
    void createCustomerWithInvalidRequestReturnsBadRequest() throws Exception {
        String invalidJson = """
                {
                  "lastName": "Lovelace",
                  "username": "ada",
                  "password": "password"
                }
                """;

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName")
                        .value("Customer firstname is required"))
                .andExpect(jsonPath("$.errors.email")
                        .value("Customer email is required"));

        verifyNoInteractions(customerService);
    }

    @Test
    void createCustomerWithInvalidEmailReturnsBadRequest() throws Exception {
        CustomerRequest request = new CustomerRequest(
                "customer-1", "Ada", "Lovelace", "invalid-email",
                validRequest().address(), Role.USER, "ada", "password"
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email")
                        .value("Customer email is not a valid email address"));

        verifyNoInteractions(customerService);
    }

    @Test
    void updateCustomerReturnsAccepted() throws Exception {
        mockMvc.perform(put(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isAccepted())
                .andExpect(content().string(""));

        verify(customerService).updateCustomer(any(CustomerRequest.class));
    }

    @Test
    void findAllReturnsCustomers() throws Exception {
        when(customerService.findAllCustomers()).thenReturn(List.of(response()));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("customer-1"))
                .andExpect(jsonPath("$[0].firstName").value("Ada"))
                .andExpect(jsonPath("$[0].username").value("ada"));

        verify(customerService).findAllCustomers();
    }

    @Test
    void existsByIdReturnsTrue() throws Exception {
        when(customerService.existsById("customer-1")).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/exists/customer-1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(customerService).existsById("customer-1");
    }

    @Test
    void existsByIdReturnsFalse() throws Exception {
        when(customerService.existsById("missing")).thenReturn(false);

        mockMvc.perform(get(BASE_URL + "/exists/missing"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(customerService).existsById("missing");
    }

    @Test
    void findByIdReturnsCustomer() throws Exception {
        when(customerService.findById("customer-1")).thenReturn(response());

        mockMvc.perform(get(BASE_URL + "/customer-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("customer-1"))
                .andExpect(jsonPath("$.firstName").value("Ada"))
                .andExpect(jsonPath("$.email").value("ada@example.com"));

        verify(customerService).findById("customer-1");
    }

    @Test
    void getCustomerByUsernameReturnsCustomer() throws Exception {
        when(customerService.findByUsername("ada"))
                .thenReturn(response());

        mockMvc.perform(get(BASE_URL + "/username/ada"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("customer-1"))
                .andExpect(jsonPath("$.firstName").value("Ada"))
                .andExpect(jsonPath("$.username").value("ada"));

        verify(customerService).findByUsername("ada");
    }

    @Test
    void getCustomerByUnknownUsernameReturnsNotFound() throws Exception {
        when(customerService.findByUsername("unknown"))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "User not found with username: unknown"
                ));

        mockMvc.perform(get(BASE_URL + "/username/unknown"))
                .andExpect(status().isNotFound());

        verify(customerService).findByUsername("unknown");
    }

    @Test
    void getCustomerForAuthenticationShouldReturnCustomerLoginResponse()
            throws Exception {
        String username = "alice";

        CustomerLoginResponse response = new CustomerLoginResponse(
                username,
                "encoded-password",
                Role.USER
        );

        when(customerService.findLoginByUsername(username))
                .thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/internal/auth/" + username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.password").value("encoded-password"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(customerService).findLoginByUsername(username);
    }

    @Test
    void deleteCustomerReturnsAccepted() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/customer-1"))
                .andExpect(status().isAccepted())
                .andExpect(content().string(""));

        verify(customerService).deleteCustomer("customer-1");
    }
}