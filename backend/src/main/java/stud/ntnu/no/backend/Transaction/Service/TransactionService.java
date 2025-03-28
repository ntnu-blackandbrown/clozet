package stud.ntnu.no.backend.Transaction.Service;

import stud.ntnu.no.backend.Transaction.DTOs.CreateTransactionRequest;
import stud.ntnu.no.backend.Transaction.DTOs.TransactionDTO;
import stud.ntnu.no.backend.Transaction.DTOs.UpdateTransactionRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    List<TransactionDTO> getAllTransactions();
    TransactionDTO getTransactionById(Long id);
    TransactionDTO createTransaction(CreateTransactionRequest dto);
    TransactionDTO updateTransaction(Long id, UpdateTransactionRequest dto);
    void deleteTransaction(Long id);
    List<TransactionDTO> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}