package stud.ntnu.no.backend.transaction.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import stud.ntnu.no.backend.transaction.dto.CreateTransactionRequest;
import stud.ntnu.no.backend.transaction.dto.TransactionDTO;
import stud.ntnu.no.backend.transaction.dto.UpdateTransactionRequest;
import stud.ntnu.no.backend.transaction.entity.Transaction;
import stud.ntnu.no.backend.transaction.exception.TransactionNotFoundException;
import stud.ntnu.no.backend.transaction.exception.TransactionValidationException;
import stud.ntnu.no.backend.transaction.mapper.TransactionMapper;
import stud.ntnu.no.backend.transaction.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Transaction transaction;
    private TransactionDTO transactionDTO;
    private CreateTransactionRequest createRequest;
    private UpdateTransactionRequest updateRequest;
    private List<Transaction> transactionList;
    private List<TransactionDTO> transactionDTOList;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testTime = LocalDateTime.now();

        // Setup test data
        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(100.0);
        transaction.setStatus("COMPLETED");
        transaction.setPaymentMethod("CREDIT_CARD");
        transaction.setCreatedAt(testTime);

        transactionDTO = new TransactionDTO();
        transactionDTO.setId(1L);
        transactionDTO.setAmount(100.0);
        transactionDTO.setStatus("COMPLETED");
        transactionDTO.setPaymentMethod("CREDIT_CARD");
        transactionDTO.setCreatedAt(testTime);

        createRequest = new CreateTransactionRequest();
        createRequest.setAmount(100.0);
        createRequest.setStatus("COMPLETED");
        createRequest.setPaymentMethod("CREDIT_CARD");

        updateRequest = new UpdateTransactionRequest();
        updateRequest.setStatus("REFUNDED");
        updateRequest.setPaymentMethod("DEBIT_CARD");

        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setAmount(200.0);
        transaction2.setStatus("PENDING");
        transaction2.setPaymentMethod("PAYPAL");
        transaction2.setCreatedAt(testTime.minusDays(1));

        TransactionDTO transactionDTO2 = new TransactionDTO();
        transactionDTO2.setId(2L);
        transactionDTO2.setAmount(200.0);
        transactionDTO2.setStatus("PENDING");
        transactionDTO2.setPaymentMethod("PAYPAL");
        transactionDTO2.setCreatedAt(testTime.minusDays(1));

        transactionList = Arrays.asList(transaction, transaction2);
        transactionDTOList = Arrays.asList(transactionDTO, transactionDTO2);
    }

    @Test
    void getAllTransactions() {
        // Arrange
        when(transactionRepository.findAll()).thenReturn(transactionList);
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);
        when(transactionMapper.toDTO(transactionList.get(1))).thenReturn(transactionDTOList.get(1));

        // Act
        List<TransactionDTO> result = transactionService.getAllTransactions();

        // Assert
        assertEquals(2, result.size());
        assertEquals(transactionDTO.getId(), result.get(0).getId());
        assertEquals(transactionDTO.getAmount(), result.get(0).getAmount());
        verify(transactionRepository, times(1)).findAll();
        verify(transactionMapper, times(2)).toDTO(any(Transaction.class));
    }

    @Test
    void getTransactionById_ExistingId_ReturnsTransaction() {
        // Arrange
        Long transactionId = 1L;
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

        // Act
        TransactionDTO result = transactionService.getTransactionById(transactionId);

        // Assert
        assertNotNull(result);
        assertEquals(transactionDTO, result);
        verify(transactionRepository, times(1)).findById(transactionId);
        verify(transactionMapper, times(1)).toDTO(transaction);
    }

    @Test
    void getTransactionById_NonExistingId_ThrowsException() {
        // Arrange
        Long transactionId = 999L;
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.getTransactionById(transactionId);
        });
        verify(transactionRepository, times(1)).findById(transactionId);
        verify(transactionMapper, never()).toDTO(any(Transaction.class));
    }

    @Test
    void createTransaction_ValidData_ReturnsCreatedTransaction() {
        // Arrange
        when(transactionMapper.toEntity(createRequest)).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

        // Act
        TransactionDTO result = transactionService.createTransaction(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(transactionDTO, result);
        verify(transactionMapper, times(1)).toEntity(createRequest);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(transactionMapper, times(1)).toDTO(transaction);
    }

    @Test
    void createTransaction_NullRequest_ThrowsException() {
        // Act & Assert
        assertThrows(TransactionValidationException.class, () -> {
            transactionService.createTransaction(null);
        });
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void updateTransaction_ExistingId_ReturnsUpdatedTransaction() {
        // Arrange
        Long transactionId = 1L;
        
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

        // Act
        TransactionDTO result = transactionService.updateTransaction(transactionId, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(transactionDTO, result);
        verify(transactionRepository, times(1)).findById(transactionId);
        verify(transactionRepository, times(1)).save(transaction);
        verify(transactionMapper, times(1)).toDTO(transaction);
    }

    @Test
    void updateTransaction_NonExistingId_ThrowsException() {
        // Arrange
        Long transactionId = 999L;
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.updateTransaction(transactionId, updateRequest);
        });
        verify(transactionRepository, times(1)).findById(transactionId);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void deleteTransaction_ExistingId_DeletesTransaction() {
        // Arrange
        Long transactionId = 1L;
        when(transactionRepository.existsById(transactionId)).thenReturn(true);
        doNothing().when(transactionRepository).deleteById(transactionId);

        // Act
        transactionService.deleteTransaction(transactionId);

        // Assert
        verify(transactionRepository, times(1)).existsById(transactionId);
        verify(transactionRepository, times(1)).deleteById(transactionId);
    }

    @Test
    void deleteTransaction_NonExistingId_ThrowsException() {
        // Arrange
        Long transactionId = 999L;
        when(transactionRepository.existsById(transactionId)).thenReturn(false);

        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.deleteTransaction(transactionId);
        });
        verify(transactionRepository, times(1)).existsById(transactionId);
        verify(transactionRepository, never()).deleteById(transactionId);
    }

    @Test
    void findByCreatedAtBetween_ReturnsMatchingTransactions() {
        // Arrange
        LocalDateTime start = testTime.minusDays(2);
        LocalDateTime end = testTime.plusDays(1);
        
        when(transactionRepository.findByCreatedAtBetween(start, end)).thenReturn(transactionList);
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);
        when(transactionMapper.toDTO(transactionList.get(1))).thenReturn(transactionDTOList.get(1));

        // Act
        List<TransactionDTO> result = transactionService.findByCreatedAtBetween(start, end);

        // Assert
        assertEquals(2, result.size());
        assertEquals(transactionDTO.getId(), result.get(0).getId());
        assertEquals(transactionDTO.getAmount(), result.get(0).getAmount());
        verify(transactionRepository, times(1)).findByCreatedAtBetween(start, end);
        verify(transactionMapper, times(2)).toDTO(any(Transaction.class));
    }
} 