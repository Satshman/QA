package udea.bancoudeaBack.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import udea.bancoudeaBack.DTO.TransactionDTO;
import udea.bancoudeaBack.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    @DisplayName("Acceptance: Should retrieve account transaction history")
    void shouldGetTransactionsForAccount() {
        TransactionDTO tx = new TransactionDTO(1L, "ACC100", "ACC200", new BigDecimal("150.00"), LocalDateTime.now());
        when(transactionService.getTransactionsForAccount("ACC100")).thenReturn(List.of(tx));

        ResponseEntity<List<TransactionDTO>> response = transactionController.getTransactionsForAccount("ACC100");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("ACC100", response.getBody().get(0).getSenderAccountNumber());
        verify(transactionService).getTransactionsForAccount("ACC100");
    }

    @Test
    @DisplayName("Acceptance: Should execute money transfer endpoint")
    void shouldTransferMoney() {
        TransactionDTO request = new TransactionDTO(null, "ACC100", "ACC200", new BigDecimal("200.00"), null);
        TransactionDTO executed = new TransactionDTO(10L, "ACC100", "ACC200", new BigDecimal("200.00"), LocalDateTime.now());

        when(transactionService.transferMoney(any(TransactionDTO.class))).thenReturn(executed);

        ResponseEntity<TransactionDTO> response = transactionController.transferMoney(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getId());
        assertEquals(new BigDecimal("200.00"), response.getBody().getAmount());
        verify(transactionService).transferMoney(request);
    }
}
