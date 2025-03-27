package stud.ntnu.no.backend.Transaction.Service;

import stud.ntnu.no.backend.Transaction.DTOs.CreateTransactionRequest;
import stud.ntnu.no.backend.Transaction.DTOs.TransactionDTO;
import stud.ntnu.no.backend.Transaction.DTOs.UpdateTransactionRequest;

import java.util.List;

public interface TransactionService {
    List<TransactionDTO> getAllTransactions();
    TransactionDTO getTransactionById(Long id);
    TransactionDTO createTransaction(CreateTransactionRequest request);
    TransactionDTO updateTransaction(Long id, UpdateTransactionRequest request);
    void deleteTransaction(Long id);
}