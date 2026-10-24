package udea.bancoudeaBack.service;

import udea.bancoudeaBack.DTO.CustomerDTO;
import udea.bancoudeaBack.entity.Customer;
import udea.bancoudeaBack.mapper.CustomerMapper;
import udea.bancoudeaBack.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public List<CustomerDTO> getAllCustomer() {
        return customerRepository.findAll().stream().map(customerMapper::toDTO).toList();
    }

    public CustomerDTO getCustomerById(Long id) {
        return customerRepository.findById(id).map(customerMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("CLiente no encontrado"));
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        validateCustomerData(customerDTO);
        Customer customer = customerMapper.toEntity(customerDTO);
        return customerMapper.toDTO(customerRepository.save(customer));
    }

    private void validateCustomerData(CustomerDTO customerDTO) {
        if (customerDTO.getFirstName() == null) {
            throw new IllegalArgumentException("First name cannot be null");
        }
        if (customerDTO.getLastName() == null) {
            throw new IllegalArgumentException("Last name cannot be null");
        }
        if (customerDTO.getAccountNumber() == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }
        if (customerDTO.getBalance() == null) {
            throw new IllegalArgumentException("Balance cannot be null");
        }
        if (customerDTO.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
    }
}
