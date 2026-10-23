package udea.bancoudeaBack.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import udea.bancoudeaBack.DTO.CustomerDTO;
import udea.bancoudeaBack.entity.Customer;
import udea.bancoudeaBack.mapper.CustomerMapper;
import udea.bancoudeaBack.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private CustomerMapper customerMapper;

	@InjectMocks
	private CustomerService customerService;

	private Customer customer;
	private Customer secondCustomer;
	private CustomerDTO customerDTO;
	private CustomerDTO secondCustomerDTO;

	@BeforeEach
	void setUp() {
		customer = new Customer(1L, "123456789", "Juan", "Perez", BigDecimal.valueOf(1500.0));
		customerDTO = new CustomerDTO(1L, "Juan", "Perez", "123456789", BigDecimal.valueOf(1500.0));
		secondCustomer = new Customer(2L, "987654321", "Maria", "Gomez", BigDecimal.valueOf(2000.0));
		secondCustomerDTO = new CustomerDTO(2L, "Maria", "Gomez", "987654321", BigDecimal.valueOf(2000.0));
	}

	@Test
	void getAllCustomerShouldReturnAllCustomers() {
		when(customerRepository.findAll()).thenReturn(List.of(customer, secondCustomer));
		when(customerMapper.toDTO(customer)).thenReturn(customerDTO);
		when(customerMapper.toDTO(secondCustomer)).thenReturn(secondCustomerDTO);

		List<CustomerDTO> result = customerService.getAllCustomer();

		assertEquals(2, result.size());
		assertEquals(customerDTO, result.get(0));
		assertEquals(secondCustomerDTO, result.get(1));
		verify(customerRepository).findAll();
	}

	@Test
	void getAllCustomerShouldReturnEmptyListWhenNoCustomersExist() {
		when(customerRepository.findAll()).thenReturn(List.of());

		List<CustomerDTO> result = customerService.getAllCustomer();

		assertEquals(0, result.size());
		verify(customerRepository).findAll();
	}

	@Test
	void getCustomerByIdShouldReturnCustomerWhenItExists() {
		when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
		when(customerMapper.toDTO(customer)).thenReturn(customerDTO);

		CustomerDTO result = customerService.getCustomerById(1L);

		assertEquals(customerDTO, result);
		verify(customerRepository).findById(1L);
	}

	@Test
	void getCustomerByIdShouldThrowWhenCustomerDoesNotExist() {
		when(customerRepository.findById(99L)).thenReturn(Optional.empty());

		RuntimeException exception = assertThrows(RuntimeException.class, () -> customerService.getCustomerById(99L));

		assertEquals("CLiente no encontrado", exception.getMessage());
		verify(customerRepository).findById(99L);
	}

	@Test
	void createCustomerShouldSaveAndReturnCreatedCustomer() {
		when(customerMapper.toEntity(customerDTO)).thenReturn(customer);
		when(customerRepository.save(customer)).thenReturn(customer);
		when(customerMapper.toDTO(customer)).thenReturn(customerDTO);

		CustomerDTO result = customerService.createCustomer(customerDTO);

		assertEquals(customerDTO, result);
		verify(customerMapper).toEntity(customerDTO);
		verify(customerRepository).save(customer);
	}

	@Test
	void createCustomerShouldThrowIllegalArgumentExceptionWhenFirstNameIsNull() {
		customerDTO.setFirstName(null);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> customerService.createCustomer(customerDTO));
		assertEquals("First name cannot be null", exception.getMessage());
	}

	@Test
	void createCustomerShouldThrowIllegalArgumentExceptionWhenLastNameIsNull() {
		customerDTO.setLastName(null);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> customerService.createCustomer(customerDTO));
		assertEquals("Last name cannot be null", exception.getMessage());
	}

	@Test
	void createCustomerShouldThrowIllegalArgumentExceptionWhenAccountNumberIsNull() {
		customerDTO.setAccountNumber(null);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> customerService.createCustomer(customerDTO));
		assertEquals("Account number cannot be null", exception.getMessage());
	}

	@Test
	void createCustomerShouldThrowIllegalArgumentExceptionWhenBalanceIsNull() {
		customerDTO.setBalance(null);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> customerService.createCustomer(customerDTO));
		assertEquals("Balance cannot be null", exception.getMessage());
	}

	@Test
	void createCustomerShouldThrowIllegalArgumentExceptionWhenBalanceIsNegative() {
		customerDTO.setBalance(BigDecimal.valueOf(-100.0));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> customerService.createCustomer(customerDTO));
		assertEquals("Balance cannot be negative", exception.getMessage());
	}

	@Test
	void createCustomerShouldThrowIllegalArgumentExceptionWhenBalanceIsZero() {
		customerDTO.setBalance(BigDecimal.ZERO);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> customerService.createCustomer(customerDTO));
		assertEquals("Balance cannot be negative", exception.getMessage());
	}
}
