package stud.ntnu.no.backend.Transaction.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import stud.ntnu.no.backend.Transaction.DTOs.CreateTransactionRequest;
import stud.ntnu.no.backend.Transaction.DTOs.TransactionDTO;
import stud.ntnu.no.backend.Transaction.DTOs.UpdateTransactionRequest;
import stud.ntnu.no.backend.Transaction.Exceptions.TransactionNotFoundException;
import stud.ntnu.no.backend.Transaction.Service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private TransactionDTO transactionDTO;
    private TransactionDTO secondTransactionDTO;
    private List<TransactionDTO> transactionDTOList;
    private CreateTransactionRequest createTransactionRequest;
    private UpdateTransactionRequest updateTransactionRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        transactionDTO = new TransactionDTO(
                1L,
                "Test Transaction",
                new BigDecimal("100.00"),
                LocalDateTime.now(),
                "COMPLETED"
        );

        secondTransactionDTO = new TransactionDTO(
                2L,
                "Another Transaction",
                new BigDecimal("200.00"),
                LocalDateTime.now(),
                "PENDING"
        );

        transactionDTOList = Arrays.asList(transactionDTO, secondTransactionDTO);

        createTransactionRequest = new CreateTransactionRequest(
                "New Transaction",
                new BigDecimal("150.00"),
                "PENDING"
        );

        updateTransactionRequest = new UpdateTransactionRequest(
                "Updated Transaction",
                new BigDecimal("175.00"),
                "COMPLETED"
        );
    }

    @Test
    void getAllTransactions_ShouldReturnListOfTransactions() {
        when(transactionService.getAllTransactions()).thenReturn(transactionDTOList);

        ResponseEntity<List<TransactionDTO>> response = transactionController.getAllTransactions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals(2L, response.getBody().get(1).getId());
        verify(transactionService, times(1)).getAllTransactions();
    }

    @Test
    void getTransactionById_WithValidId_ShouldReturnTransaction() {
        when(transactionService.getTransactionById(1L)).thenReturn(transactionDTO);

        ResponseEntity<TransactionDTO> response = transactionController.getTransactionById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Transaction", response.getBody().getDescription());
        verify(transactionService, times(1)).getTransactionById(1L);
    }

    @Test
    void getTransactionById_WithInvalidId_ShouldThrowException() {
        when(transactionService.getTransactionById(99L)).thenThrow(new TransactionNotFoundException(99L));

        try {
            transactionController.getTransactionById(99L);
        } catch (TransactionNotFoundException e) {
            assertEquals("Transaction not found with id: 99", e.getMessage());
        }

        verify(transactionService, times(1)).getTransactionById(99L);
    }

    @Test
    void createTransaction_WithValidData_ShouldReturnCreatedTransaction() {
        when(transactionService.createTransaction(any(CreateTransactionRequest.class))).thenReturn(transactionDTO);

        ResponseEntity<TransactionDTO> response = transactionController.createTransaction(createTransactionRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(transactionService, times(1)).createTransaction(any(CreateTransactionRequest.class));
    }

    @Test
    void updateTransaction_WithValidData_ShouldReturnUpdatedTransaction() {
        when(transactionService.updateTransaction(eq(1L), any(UpdateTransactionRequest.class))).thenReturn(transactionDTO);

        ResponseEntity<TransactionDTO> response = transactionController.updateTransaction(1L, updateTransactionRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(transactionService, times(1)).updateTransaction(eq(1L), any(UpdateTransactionRequest.class));
    }

    @Test
    void deleteTransaction_ShouldReturnNoContent() {
        doNothing().when(transactionService).deleteTransaction(1L);

        ResponseEntity<Void> response = transactionController.deleteTransaction(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(transactionService, times(1)).deleteTransaction(1L);
    }
}