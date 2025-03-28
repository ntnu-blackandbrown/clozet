package stud.ntnu.no.backend.Transaction.Service;

import stud.ntnu.no.backend.Transaction.DTOs.CreateTransactionDTO;
import stud.ntnu.no.backend.Transaction.DTOs.TransactionDTO;
import stud.ntnu.no.backend.Transaction.DTOs.UpdateTransactionDTO;

import java.util.List;

public interface TransactionService {
    List<TransactionDTO> getAllTransactions();
    TransactionDTO getTransactionById(Long id);
    TransactionDTO createTransaction(CreateTransactionDTO dto);
    TransactionDTO updateTransaction(Long id, UpdateTransactionDTO dto);
    void deleteTransaction(Long id);
}