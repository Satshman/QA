package udea.bancoudeaBack.service;

import udea.bancoudeaBack.DTO.TransactionDTO;
import udea.bancoudeaBack.entity.Customer;
import udea.bancoudeaBack.entity.Transaction;
import udea.bancoudeaBack.repository.CustomerRepository;
import udea.bancoudeaBack.repository.TransactionRepository;
import udea.bancoudeaBack.mapper.TransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.math.BigDecimal;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              CustomerRepository customerRepository,
                              TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
        this.transactionMapper = transactionMapper;
    }

    @Transactional
    public TransactionDTO transferMoney(TransactionDTO transactionDTO) {
        validateTransactionRequest(transactionDTO);

        Customer sender = customerRepository.findByAccountNumber(transactionDTO.getSenderAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Sender Account Number not found"));

        Customer receiver = customerRepository.findByAccountNumber(transactionDTO.getReceiverAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Receiver Account Number not found"));

        validateSufficientBalance(sender, transactionDTO.getAmount());

        // Realiza la transferencia
        sender.setBalance(sender.getBalance().subtract(transactionDTO.getAmount()));
        receiver.setBalance(receiver.getBalance().add(transactionDTO.getAmount()));

        // Guardar los cambios en las cuentas
        customerRepository.save(sender);
        customerRepository.save(receiver);

        // Fecha de transferencia
        LocalDateTime timestamp = LocalDateTime.now();

        // Crear y Guardar la transaccion
        Transaction transaction = new Transaction();
        transaction.setSenderAccountNumber(sender.getAccountNumber());
        transaction.setReceiverAccountNumber(receiver.getAccountNumber());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setTimestamp(timestamp);
        transaction = transactionRepository.save(transaction);

        return transactionMapper.toDTO(transaction);
    }

    private void validateTransactionRequest(TransactionDTO transactionDTO) {
        if (transactionDTO.getSenderAccountNumber() == null) {
            throw new IllegalArgumentException("Sender account number cannot be null");
        }
        if (transactionDTO.getReceiverAccountNumber() == null) {
            throw new IllegalArgumentException("Receiver account number cannot be null");
        }
        if (Objects.equals(transactionDTO.getSenderAccountNumber(), transactionDTO.getReceiverAccountNumber())) {
            throw new IllegalArgumentException("Sender and Receiver account numbers cannot be the same");
        }
        if (transactionDTO.getAmount() == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (transactionDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount cannot be negative or zero");
        }
    }

    private void validateSufficientBalance(Customer sender, BigDecimal amount) {
        if (sender.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Sender balance not enough");
        }
    }

    public List<TransactionDTO> getTransactionsForAccount(String accountNumber) {
        if (accountNumber == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }

        return transactionRepository.findBySenderAccountNumberOrReceiverAccountNumber(accountNumber, accountNumber)
                .stream().map(transactionMapper::toDTO).toList();
    }
}
