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
import java.util.stream.Collectors;
import java.math.BigDecimal;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Transactional
    public TransactionDTO transferMoney(TransactionDTO transactionDTO) {
        // validar que los numeros de cuentas no sean nulos
        if (transactionDTO.getSenderAccountNumber() == null) {
            throw new IllegalArgumentException("Sender account number cannot be null");
        }

        if (transactionDTO.getReceiverAccountNumber() == null) {
            throw new IllegalArgumentException("Receiver account number cannot be null");
        }

        // Validar que los numeros de cuentas no sean iguales
        if (Objects.equals(transactionDTO.getSenderAccountNumber(), transactionDTO.getReceiverAccountNumber())) {
            throw new IllegalArgumentException("Sender and Receiver account numbers cannot be the same");
        }

        // Validar que el monto no sea nulo
        if (transactionDTO.getAmount() == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        // Validar que el monto sea mayor a 0
        if (transactionDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount cannot be negative or zero");
        }

        // Buscar los clientes por numero de cuenta
        Customer sender = customerRepository.findByAccountNumber(transactionDTO.getSenderAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Sender Account Number not found"));

        Customer receiver = customerRepository.findByAccountNumber(transactionDTO.getReceiverAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Receiver Account Number not found"));

        // Validar que el remitente tenga saldo suficiente
        if (sender.getBalance().subtract(transactionDTO.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Sender balance not enough");
        }

        // Realiza la transferencia
        sender.setBalance(sender.getBalance().subtract(transactionDTO.getAmount()));
        receiver.setBalance(receiver.getBalance().add(transactionDTO.getAmount()));

        // Guardar los cambios en las cuentas
        customerRepository.save(sender);
        customerRepository.save(receiver);

        // Fecha de tranferencia
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

    public List<TransactionDTO> getTransactionsForAccount(String accountNumber) {
        // Validar que el numero de cuenta no sea nulo
        if (accountNumber == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }

        // Buscar las transacciones por numero de cuenta
        return transactionRepository.findBySenderAccountNumberOrReceiverAccountNumber(accountNumber, accountNumber)
                .stream().map(transactionMapper::toDTO).toList();
    }
}
