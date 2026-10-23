package udea.bancoudeaBack.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import udea.bancoudeaBack.DTO.TransactionDTO;
import udea.bancoudeaBack.entity.Transaction;
import udea.bancoudeaBack.entity.Customer;
import udea.bancoudeaBack.mapper.TransactionMapper;
import udea.bancoudeaBack.repository.TransactionRepository;
import udea.bancoudeaBack.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

        @Mock
        private TransactionRepository transactionRepository;

        @Mock
        private CustomerRepository customerRepository;

        @Mock
        private TransactionMapper transactionMapper;

        @InjectMocks
        private TransactionService transactionService;

        private Customer sender;
        private Customer receiver;
        private TransactionDTO transactionDTO;
        private Transaction savedTransaction;
        private TransactionDTO savedTransactionDTO;
        private LocalDateTime timestamp;
        private Transaction savedTransactionTwo;
        private TransactionDTO savedTransactionTwoDTO;

        @BeforeEach
        void setUp() {
                timestamp = LocalDateTime.now();
                sender = new Customer(1L, "123456789", "Juan", "Perez", BigDecimal.valueOf(1500.0));
                receiver = new Customer(2L, "987654321", "Maria", "Gomez", BigDecimal.valueOf(2000.0));
                transactionDTO = new TransactionDTO(null, "123456789", "987654321", BigDecimal.valueOf(100.0), null);
                savedTransaction = new Transaction(1L, "123456789", "987654321", BigDecimal.valueOf(100.0), timestamp);
                savedTransactionDTO = new TransactionDTO(1L, "123456789", "987654321", BigDecimal.valueOf(100.0),
                                timestamp);
                savedTransactionTwo = new Transaction(2L, "987654321", "123456789", BigDecimal.valueOf(50.0),
                                timestamp.plusMinutes(1));
                savedTransactionTwoDTO = new TransactionDTO(2L, "987654321", "123456789", BigDecimal.valueOf(50.0),
                                timestamp.plusMinutes(1));
        }

        @Test
        void transferMoneyShouldTransferMoneySuccessfully() {
                when(customerRepository.findByAccountNumber("123456789")).thenReturn(Optional.of(sender));
                when(customerRepository.findByAccountNumber("987654321")).thenReturn(Optional.of(receiver));
                when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
                when(transactionMapper.toDTO(savedTransaction)).thenReturn(savedTransactionDTO);

                TransactionDTO result = transactionService.transferMoney(transactionDTO);

                assertEquals(0, BigDecimal.valueOf(1400.0).compareTo(sender.getBalance()));
                assertEquals(0, BigDecimal.valueOf(2100.0).compareTo(receiver.getBalance()));
                assertEquals(savedTransactionDTO, result);

                verify(customerRepository).findByAccountNumber("123456789");
                verify(customerRepository).findByAccountNumber("987654321");
                verify(customerRepository).save(sender);
                verify(customerRepository).save(receiver);

                ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

                verify(transactionRepository).save(transactionCaptor.capture());

                Transaction capturedTransaction = transactionCaptor.getValue();

                assertEquals("123456789", capturedTransaction.getSenderAccountNumber());
                assertEquals("987654321", capturedTransaction.getReceiverAccountNumber());
                assertEquals(0, BigDecimal.valueOf(100.0).compareTo(capturedTransaction.getAmount()));
                assertNotNull(capturedTransaction.getTimestamp());
        }

        @Test
        void transferMoneyShouldThrowWhenSenderAccountNumberIsNull() {
                transactionDTO.setSenderAccountNumber(null);

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                () -> transactionService.transferMoney(transactionDTO));

                assertEquals("Sender account number cannot be null", exception.getMessage());
                verifyNoInteractions(customerRepository);
                verifyNoInteractions(transactionRepository);
        }

        @Test
        void transferMoneyShouldThrowWhenReceiverAccountNumberIsNull() {
                transactionDTO.setReceiverAccountNumber(null);

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                () -> transactionService.transferMoney(transactionDTO));

                assertEquals("Receiver account number cannot be null", exception.getMessage());
                verifyNoInteractions(customerRepository);
                verifyNoInteractions(transactionRepository);
        }

        @Test
        void transferMoneyShouldThrowWhenSenderAndReceiverAreTheSame() {
                transactionDTO.setReceiverAccountNumber("123456789");

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                () -> transactionService.transferMoney(transactionDTO));

                assertEquals("Sender and Receiver account numbers cannot be the same", exception.getMessage());
                verifyNoInteractions(customerRepository);
                verifyNoInteractions(transactionRepository);
        }

        @Test
        void transferMoneyShouldThrowWhenAmountIsNull() {
                transactionDTO.setAmount(null);

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                () -> transactionService.transferMoney(transactionDTO));

                assertEquals("Amount cannot be null", exception.getMessage());
                verifyNoInteractions(customerRepository);
                verifyNoInteractions(transactionRepository);
        }

        @Test
        void transferMoneyShouldThrowWhenAmountIsNegative() {
                transactionDTO.setAmount(BigDecimal.valueOf(-10.0));

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                () -> transactionService.transferMoney(transactionDTO));

                assertEquals("Amount cannot be negative or zero", exception.getMessage());
                verifyNoInteractions(customerRepository);
                verifyNoInteractions(transactionRepository);
        }

        @Test
        void transferMoneyShouldThrowWhenAmountIsZero() {
                transactionDTO.setAmount(BigDecimal.ZERO);

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                () -> transactionService.transferMoney(transactionDTO));

                assertEquals("Amount cannot be negative or zero", exception.getMessage());
                verifyNoInteractions(customerRepository);
                verifyNoInteractions(transactionRepository);
        }

        @Test
        void transferMoneyShouldThrowWhenSenderIsNotFound() {
                transactionDTO.setSenderAccountNumber("321");
                when(customerRepository.findByAccountNumber("321")).thenReturn(Optional.empty());

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                () -> transactionService.transferMoney(transactionDTO));

                assertEquals("Sender Account Number not found", exception.getMessage());
                verify(customerRepository).findByAccountNumber("321");
                verify(customerRepository, never()).findByAccountNumber("987654321");
                verifyNoInteractions(transactionRepository);
        }

        @Test
        void transferMoneyShouldThrowWhenReceiverIsNotFound() {
                transactionDTO.setReceiverAccountNumber("987");
                when(customerRepository.findByAccountNumber("123456789")).thenReturn(Optional.of(sender));
                when(customerRepository.findByAccountNumber("987")).thenReturn(Optional.empty());

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                () -> transactionService.transferMoney(transactionDTO));

                assertEquals("Receiver Account Number not found", exception.getMessage());
                verify(customerRepository).findByAccountNumber("123456789");
                verify(customerRepository).findByAccountNumber("987");
                verify(customerRepository, never()).save(any(Customer.class));
                verifyNoInteractions(transactionRepository);
        }

        @Test
        void transferMoneyShouldThrowWhenSenderHasInsufficientBalance() {
                sender.setBalance(BigDecimal.valueOf(50.0));
                when(customerRepository.findByAccountNumber("123456789")).thenReturn(Optional.of(sender));
                when(customerRepository.findByAccountNumber("987654321")).thenReturn(Optional.of(receiver));

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                () -> transactionService.transferMoney(transactionDTO));

                assertEquals("Sender balance not enough", exception.getMessage());
                verify(customerRepository).findByAccountNumber("123456789");
                verify(customerRepository).findByAccountNumber("987654321");
                verify(customerRepository, never()).save(any(Customer.class));
                verifyNoInteractions(transactionRepository);
        }

        @Test
        void transferMoneyShouldThrowWhenSenderHasExactBalance() {
                sender.setBalance(BigDecimal.valueOf(100.0));
                when(customerRepository.findByAccountNumber("123456789")).thenReturn(Optional.of(sender));
                when(customerRepository.findByAccountNumber("987654321")).thenReturn(Optional.of(receiver));
                when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
                when(transactionMapper.toDTO(savedTransaction)).thenReturn(savedTransactionDTO);

                assertDoesNotThrow(() -> transactionService.transferMoney(transactionDTO));

                verify(customerRepository).findByAccountNumber("123456789");
                verify(customerRepository).findByAccountNumber("987654321");
                verify(customerRepository).save(sender);
                verify(customerRepository).save(receiver);
                verify(transactionRepository).save(any(Transaction.class));
                verify(transactionMapper).toDTO(any(Transaction.class));
        }

        @Test
        void getTransactionsForAccountShouldReturnTransactionsForGivenAccount() {
                when(transactionRepository.findBySenderAccountNumberOrReceiverAccountNumber("123456789", "123456789"))
                                .thenReturn(List.of(savedTransaction, savedTransactionTwo));
                when(transactionMapper.toDTO(savedTransaction)).thenReturn(savedTransactionDTO);
                when(transactionMapper.toDTO(savedTransactionTwo)).thenReturn(savedTransactionTwoDTO);

                List<TransactionDTO> result = transactionService.getTransactionsForAccount("123456789");

                assertEquals(2, result.size());
                assertEquals(savedTransactionDTO, result.get(0));
                assertEquals(savedTransactionTwoDTO, result.get(1));
                verify(transactionRepository).findBySenderAccountNumberOrReceiverAccountNumber("123456789",
                                "123456789");
        }

        @Test
        void getTransactionsForAccountShouldReturnEmptyListWhenNoTransactionsExist() {
                when(transactionRepository.findBySenderAccountNumberOrReceiverAccountNumber("123", "123"))
                                .thenReturn(List.of());

                List<TransactionDTO> result = transactionService.getTransactionsForAccount("123");

                assertEquals(0, result.size());
                verify(transactionRepository).findBySenderAccountNumberOrReceiverAccountNumber("123", "123");
        }

        @Test
        void getTransactionsForAccountShouldThrowWhenAccountNumberIsNull() {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                () -> transactionService.getTransactionsForAccount(null));

                assertEquals("Account number cannot be null", exception.getMessage());
                verifyNoInteractions(transactionRepository);
        }
}
