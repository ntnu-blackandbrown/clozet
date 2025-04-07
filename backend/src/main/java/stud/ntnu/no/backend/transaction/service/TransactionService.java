package stud.ntnu.no.backend.transaction.service;

import stud.ntnu.no.backend.transaction.dto.CreateTransactionRequest;
import stud.ntnu.no.backend.transaction.dto.TransactionDTO;
import stud.ntnu.no.backend.transaction.dto.UpdateTransactionRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing transactions.
 * <p>
 * This interface defines methods for CRUD operations on transactions.
 */
public interface TransactionService {
    /**
     * Retrieves all transactions.
     *
     * @return a list of TransactionDTOs
     */
    List<TransactionDTO> getAllTransactions();

    /**
     * Retrieves a transaction by its ID.
     *
     * @param id the ID of the transaction
     * @return the TransactionDTO
     */
    TransactionDTO getTransactionById(Long id);

    /**
     * Creates a new transaction.
     *
     * @param dto the CreateTransactionRequest
     * @return the created TransactionDTO
     */
    TransactionDTO createTransaction(CreateTransactionRequest dto);

    /**
     * Updates an existing transaction.
     *
     * @param id the ID of the transaction to update
     * @param dto the UpdateTransactionRequest with updated information
     * @return the updated TransactionDTO
     */
    TransactionDTO updateTransaction(Long id, UpdateTransactionRequest dto);

    /**
     * Deletes a transaction by its ID.
     *
     * @param id the ID of the transaction to delete
     */
    void deleteTransaction(Long id);

    /**
     * Retrieves transactions created between the specified dates.
     *
     * @param start the start date and time
     * @param end the end date and time
     * @return a list of TransactionDTOs
     */
    List<TransactionDTO> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Retrieves transactions by buyerId.
     *
     * @param buyerId the ID of the buyer
     * @return a list of TransactionDTOs
     */
    List<TransactionDTO> getTransactionsByBuyerId(String buyerId);

    /**
     * Handles the purchase transaction and deactivates the item.
     *
     * @param dto the CreateTransactionRequest
     * @return the created TransactionDTO
     */
    TransactionDTO handlePurchaseTransaction(CreateTransactionRequest dto);
}