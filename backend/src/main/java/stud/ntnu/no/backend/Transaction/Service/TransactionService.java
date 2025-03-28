package stud.ntnu.no.backend.Transaction.Service;

import stud.ntnu.no.backend.Transaction.DTOs.*;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    List<TransactionDTO> getAllTransactions();
    TransactionDTO getTransactionById(Long id);
    TransactionDTO createTransaction(CreateTransactionRequest dto);
    TransactionDTO updateTransaction(Long id, UpdateTransactionRequest dto);

    TransactionDTO createTransaction(CreateTransactionDTO dto);

    TransactionDTO updateTransaction(Long id, UpdateTransactionDTO dto);

    void deleteTransaction(Long id);
    
    // Add this method to match the repository method
    List<TransactionDTO> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}