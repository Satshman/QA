package udea.bancoudeaBack.service;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import udea.bancoudeaBack.repository.CustomerRepository;
import udea.bancoudeaBack.mapper.CustomerMapper;
import udea.bancoudeaBack.entity.Customer;
import udea.bancoudeaBack.DTO.CustomerDTO;

import org.mockito.Mockito;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(2)
public class CustomerServiceBenchmark {

    private CustomerService customerService;

    @Setup
    public void setup() {
        CustomerRepository repoMock = Mockito.mock(CustomerRepository.class);
        CustomerMapper mapperMock = Mockito.mock(CustomerMapper.class);

        // Simulamos una base de datos con 1000 clientes
        List<Customer> customers = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            customers.add(new Customer());
        }

        // findAll() devuelve los 1000 clientes
        Mockito.when(repoMock.findAll())
                .thenReturn(customers);

        // findById() devuelve un cliente válido
        Mockito.when(repoMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Customer()));

        // El mapper devuelve un DTO válido
        Mockito.when(mapperMock.toDTO(Mockito.any(Customer.class)))
                .thenReturn(new CustomerDTO());

        this.customerService = new CustomerService(repoMock, mapperMock);
    }

    @Benchmark
    public void testGetAllCustomers(Blackhole bh) {
        bh.consume(customerService.getAllCustomer());
    }

    @Benchmark
    public void testGetCustomerById(Blackhole bh) {
        bh.consume(customerService.getCustomerById(1L));
    }
}