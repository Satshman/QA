package udea.bancoudeaBack.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import udea.bancoudeaBack.DTO.CustomerDTO;
import udea.bancoudeaBack.service.CustomerService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    @Test
    @DisplayName("Acceptance: Should return all customers with HTTP 200")
    void shouldReturnAllCustomers() {
        CustomerDTO customer = new CustomerDTO(1L, "Carlos", "Perez", "ACC123", new BigDecimal("1000.00"));
        when(customerService.getAllCustomer()).thenReturn(List.of(customer));

        ResponseEntity<List<CustomerDTO>> response = customerController.getAllCustomers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Carlos", response.getBody().get(0).getFirstName());
        verify(customerService).getAllCustomer();
    }

    @Test
    @DisplayName("Acceptance: Should return customer by ID with HTTP 200")
    void shouldReturnCustomerById() {
        CustomerDTO customer = new CustomerDTO(1L, "Carlos", "Perez", "ACC123", new BigDecimal("1000.00"));
        when(customerService.getCustomerById(1L)).thenReturn(customer);

        ResponseEntity<CustomerDTO> response = customerController.getCustomerById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(customerService).getCustomerById(1L);
    }

    @Test
    @DisplayName("Acceptance: Should create customer with HTTP 200")
    void shouldCreateCustomer() {
        CustomerDTO request = new CustomerDTO(null, "Maria", "Lopez", "ACC456", new BigDecimal("500.00"));
        CustomerDTO created = new CustomerDTO(2L, "Maria", "Lopez", "ACC456", new BigDecimal("500.00"));
        when(customerService.createCustomer(any(CustomerDTO.class))).thenReturn(created);

        ResponseEntity<CustomerDTO> response = customerController.createCustomer(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getId());
        assertEquals("ACC456", response.getBody().getAccountNumber());
        verify(customerService).createCustomer(request);
    }
}
