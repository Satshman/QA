package udea.bancoudeaBack.controller;

import udea.bancoudeaBack.DTO.TransactionDTO;
import udea.bancoudeaBack.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionFacade;

    public TransactionController(TransactionService transactionFacade) {
        this.transactionFacade = transactionFacade;
    }

    // Obtener todas transacciones por numero de cuenta
    @GetMapping("/{accountNumber}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsForAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(transactionFacade.getTransactionsForAccount(accountNumber));
    }

    // Tranferir plata
    @PostMapping
    public ResponseEntity<TransactionDTO> transferMoney(@RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(transactionFacade.transferMoney(transactionDTO));
    }
}
