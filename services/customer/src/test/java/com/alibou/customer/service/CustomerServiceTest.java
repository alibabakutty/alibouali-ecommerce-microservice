
package com.alibou.customer.service;

import com.alibou.customer.dto.CustomerAuthResponse;
import com.alibou.customer.dto.CustomerLoginResponse;
import com.alibou.customer.dto.CustomerRequest;
import com.alibou.customer.entity.Address;
import com.alibou.customer.entity.Customer;
import com.alibou.customer.entity.Role;
import com.alibou.customer.exception.CustomerNotFoundException;
import com.alibou.customer.mapper.CustomerMapper;
import com.alibou.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private CustomerMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerRequest request;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId("customer-123");
        customer.setFirstname("John");
        customer.setLastname("Doe");
        customer.setEmail("john@example.com");
        customer.setUsername("johndoe");
        customer.setPassword("encoded-old-password");

        request = new CustomerRequest(
                "customer-123",       // id
                "John",               // firstName
                "Doe",                // lastName
                "john@example.com",   // email
                null,                 // address
                Role.USER,            // role
                "johndoe",            // username
                "plain-password"      // password
        );
    }

    @Test
    void createCustomer_ShouldEncodePassword_SaveCustomer_AndReturnId() {
        when(mapper.toCustomer(request)).thenReturn(customer);
        when(passwordEncoder.encode("plain-password"))
                .thenReturn("encoded-password");

        String result = customerService.createCustomer(request);

        assertEquals("customer-123", result);
        assertEquals("encoded-password", customer.getPassword());
        assertEquals(Role.USER, customer.getRole());

        verify(mapper).toCustomer(request);
        verify(passwordEncoder).encode("plain-password");
        verify(repository).save(customer);
    }

    @Test
    void updateCustomer_ShouldUpdateFieldsAndSaveCustomer() {
        when(repository.findById("customer-123"))
                .thenReturn(Optional.of(customer));
        when(passwordEncoder.encode("plain-password"))
                .thenReturn("encoded-password");

        customerService.updateCustomer(request);

        assertEquals("John", customer.getFirstname());
        assertEquals("Doe", customer.getLastname());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals("johndoe", customer.getUsername());
        assertEquals("encoded-password", customer.getPassword());

        verify(repository).findById("customer-123");
        verify(passwordEncoder).encode("plain-password");
        verify(repository).save(customer);
    }

    @Test
    void updateCustomer_ShouldThrowException_WhenCustomerDoesNotExist() {
        when(repository.findById("customer-123"))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.updateCustomer(request)
        );

        verify(repository, never()).save(any(Customer.class));
    }

    @Test
    void updateCustomer_ShouldPreserveExistingValuesWhenFieldsAreBlank() {
        when(repository.findById("customer-123"))
                .thenReturn(Optional.of(customer));

        CustomerRequest updateRequest = new CustomerRequest(
                "customer-123",
                " ",
                "",
                " ",
                null,
                null,
                "",
                ""
        );

        customerService.updateCustomer(updateRequest);

        assertEquals("John", customer.getFirstname());
        assertEquals("Doe", customer.getLastname());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals("johndoe", customer.getUsername());
        assertEquals("encoded-old-password", customer.getPassword());

        verify(passwordEncoder, never()).encode(anyString());
        verify(repository).save(customer);
    }

    @Test
    void updateCustomer_shouldReplaceAddressWhenNewAddressIsProvided() {
        Customer existingCustomer = mock(Customer.class);
        Address newAddress = mock(Address.class);

        when(repository.findById("customer-123"))
                .thenReturn(Optional.of(existingCustomer));

        CustomerRequest updateRequest = new CustomerRequest(
                "customer-123",
                "John",
                "Doe",
                "john@example.com",
                newAddress,
                null,
                "johndoe",
                ""                   // Don't change password
        );

        customerService.updateCustomer(updateRequest);

        verify(existingCustomer).setAddress(newAddress);
    }

    @Test
    void updateCustomer_shouldNotEncodeBlankPassword() {
        Customer existingCustomer = mock(Customer.class);

        when(repository.findById("customer-123"))
                .thenReturn(Optional.of(existingCustomer));

        CustomerRequest updateRequest = new CustomerRequest(
                "customer-123",
                "John",
                "Doe",
                "john@example.com",
                null,
                null,
                "johndoe",
                " "
        );

        customerService.updateCustomer(updateRequest);

        verify(passwordEncoder, never()).encode(anyString());
        verify(existingCustomer, never()).setPassword(anyString());
    }

    @Test
    void createCustomer_shouldSaveRequestedRoleAndEncodedPassword() {
        Customer newCustomer = mock(Customer.class);

        when(mapper.toCustomer(any(CustomerRequest.class)))
                .thenReturn(newCustomer);

        when(passwordEncoder.encode("plain-password"))
                .thenReturn("encoded-password");

        CustomerRequest createRequest = new CustomerRequest(
                null,
                "John",
                "Doe",
                "john@example.com",
                null,
                Role.ADMIN,
                "johndoe",
                "plain-password"
        );

        customerService.createCustomer(createRequest);

        verify(passwordEncoder).encode("plain-password");
        verify(newCustomer).setPassword("encoded-password");
        verify(newCustomer).setRole(Role.ADMIN);
        verify(repository).save(newCustomer);
    }

    @Test
    void deleteCustomer_shouldCallRepositoryWithCorrectId() {
        String customerId = "customer-123";

        customerService.deleteCustomer(customerId);

        verify(repository).deleteById(customerId);
    }

    @Test
    void findAllCustomers_ShouldReturnMappedCustomers() {
        CustomerAuthResponse response = mock(CustomerAuthResponse.class);

        when(repository.findAll()).thenReturn(List.of(customer));
        when(mapper.fromCustomer(customer)).thenReturn(response);

        List<CustomerAuthResponse> result =
                customerService.findAllCustomers();

        assertEquals(1, result.size());
        assertSame(response, result.get(0));

        verify(repository).findAll();
        verify(mapper).fromCustomer(customer);
    }

    @Test
    void existsById_ShouldReturnTrue_WhenCustomerExists() {
        when(repository.findById("customer-123"))
                .thenReturn(Optional.of(customer));

        Boolean result = customerService.existsById("customer-123");

        assertTrue(result);
        verify(repository).findById("customer-123");
    }

    @Test
    void existsById_ShouldReturnFalse_WhenCustomerDoesNotExist() {
        when(repository.findById("missing-id"))
                .thenReturn(Optional.empty());

        Boolean result = customerService.existsById("missing-id");

        assertFalse(result);
        verify(repository).findById("missing-id");
    }

    @Test
    void findById_ShouldReturnMappedCustomer_WhenCustomerExists() {
        CustomerAuthResponse response = mock(CustomerAuthResponse.class);

        when(repository.findById("customer-123"))
                .thenReturn(Optional.of(customer));
        when(mapper.fromCustomer(customer)).thenReturn(response);

        CustomerAuthResponse result =
                customerService.findById("customer-123");

        assertSame(response, result);

        verify(repository).findById("customer-123");
        verify(mapper).fromCustomer(customer);
    }


    @Test
    void findByUsername_shouldReturnCustomer_whenUsernameExists() {
        Customer customer = Customer.builder()
                .id("customer-1")
                .firstname("Ada")
                .lastname("Lovelace")
                .email("ada@example.com")
                .role(Role.USER)
                .username("ada")
                .password("encoded-password")
                .build();

        CustomerAuthResponse expected = new CustomerAuthResponse(
                "customer-1",
                "Ada",
                "Lovelace",
                "ada@example.com",
                customer.getAddress(),
                Role.USER,
                "ada"
        );

        when(repository.findByUsername("ada"))
                .thenReturn(Optional.of(customer));
        when(mapper.fromCustomer(customer))
                .thenReturn(expected);

        CustomerAuthResponse actual = customerService.findByUsername("ada");

        assertEquals(expected, actual);
        verify(repository).findByUsername("ada");
        verify(mapper).fromCustomer(customer);
    }


    @Test
    void findByUsername_shouldThrowNotFound_whenUsernameDoesNotExist() {
        when(repository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.findByUsername("unknown")
        );

        assertEquals(
                "No customer found with username: unknown",
                exception.getMsg()
        );

        verify(repository).findByUsername("unknown");
    }



    @Test
    void findById_ShouldThrowException_WhenCustomerDoesNotExist() {
        when(repository.findById("missing-id"))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.findById("missing-id")
        );

        verify(mapper, never()).fromCustomer(any(Customer.class));
    }

    @Test
    void findLoginByUsernameShouldReturnLoginResponse() {
        Customer customer = Customer.builder()
                .username("alice")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        when(repository.findByUsername("alice"))
                .thenReturn(Optional.of(customer));

        CustomerLoginResponse result =
                customerService.findLoginByUsername("alice");

        assertEquals("alice", result.username());
        assertEquals("encoded-password", result.password());
        assertEquals(Role.USER, result.role());

        verify(repository).findByUsername("alice");
    }

    @Test
    void findLoginByUsernameShouldThrowWhenCustomerNotFound() {
        when(repository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.findLoginByUsername("missing")
        );

        verify(repository).findByUsername("missing");
    }

    @Test
    void deleteCustomer_ShouldDeleteById() {
        customerService.deleteCustomer("customer-123");

        verify(repository).deleteById("customer-123");
    }
}
