package stud.ntnu.no.backend.transaction.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.transaction.dto.CreateTransactionRequest;
import stud.ntnu.no.backend.transaction.dto.TransactionDTO;
import stud.ntnu.no.backend.transaction.dto.UpdateTransactionRequest;
import stud.ntnu.no.backend.transaction.service.TransactionService;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for managing transaction-related operations.
 * <p>
 * This controller provides RESTful API endpoints for creating, retrieving, updating,
 * and deleting transactions. It handles requests related to buyers purchasing items
 * from sellers, tracking transaction history, and managing the transaction lifecycle.
 * </p>
 * <p>
 * All endpoints return appropriate HTTP status codes and structured response data
 * using the {@link TransactionDTO} format.
 * </p>
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    /**
     * Constructs a new TransactionController with the specified service.
     *
     * @param transactionService the service for handling transaction business logic
     */
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    
    /**
     * Retrieves all transactions in the system.
     * <p>
     * This endpoint returns a collection of all transactions, sorted by creation date
     * in descending order by default.
     * </p>
     *
     * @return a ResponseEntity containing a list of TransactionDTOs and HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        logger.info("Fetching all transactions");
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }
    
    /**
     * Retrieves a specific transaction by its unique identifier.
     * <p>
     * This endpoint attempts to find and return a transaction that matches the provided ID.
     * </p>
     *
     * @param id the unique identifier of the transaction
     * @return a ResponseEntity containing the TransactionDTO and HTTP status 200 (OK)
     * @throws stud.ntnu.no.backend.transaction.exception.TransactionNotFoundException if no transaction with the given ID exists
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        logger.info("Fetching transaction with ID: {}", id);
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }
    
    /**
     * Creates a new transaction in the system.
     * <p>
     * This endpoint processes the request to create a new transaction record.
     * The transaction will be associated with the specified buyer, seller, and item.
     * </p>
     *
     * @param dto the data transfer object containing transaction details
     * @return a ResponseEntity containing the created TransactionDTO and HTTP status 201 (Created)
     * @throws stud.ntnu.no.backend.user.exception.UserNotFoundException if the buyer or seller does not exist
     * @throws stud.ntnu.no.backend.item.exception.ItemNotFoundException if the item does not exist
     * @throws stud.ntnu.no.backend.item.exception.ItemNotAvailableException if the item is not available for purchase
     */
    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody CreateTransactionRequest dto) {
        logger.info("Creating new transaction");
        return new ResponseEntity<>(transactionService.createTransaction(dto), HttpStatus.CREATED);
    }
    
    /**
     * Updates an existing transaction.
     * <p>
     * This endpoint updates a transaction with the provided data. 
     * Only specific fields can be updated after a transaction is created, 
     * such as the status, payment details, or shipping information.
     * </p>
     *
     * @param id the unique identifier of the transaction to update
     * @param dto the data transfer object containing updated transaction information
     * @return a ResponseEntity containing the updated TransactionDTO and HTTP status 200 (OK)
     * @throws stud.ntnu.no.backend.transaction.exception.TransactionNotFoundException if no transaction with the given ID exists
     * @throws stud.ntnu.no.backend.transaction.exception.TransactionValidationException if the update violates business rules
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Long id, @RequestBody UpdateTransactionRequest dto) {
        logger.info("Updating transaction with ID: {}", id);
        return ResponseEntity.ok(transactionService.updateTransaction(id, dto));
    }
    
    /**
     * Deletes a transaction by its unique identifier.
     * <p>
     * This endpoint removes a transaction from the system. Depending on the implementation,
     * this may be a soft delete (marking as inactive) or a hard delete.
     * </p>
     *
     * @param id the unique identifier of the transaction to delete
     * @return a ResponseEntity with HTTP status 204 (No Content)
     * @throws stud.ntnu.no.backend.transaction.exception.TransactionNotFoundException if no transaction with the given ID exists
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        logger.info("Deleting transaction with ID: {}", id);
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves transactions created between the specified dates.
     * <p>
     * This endpoint filters transactions based on their creation timestamp falling
     * within the specified date range.
     * </p>
     *
     * @param start the start date and time (inclusive) in ISO 8601 format
     * @param end the end date and time (inclusive) in ISO 8601 format
     * @return a ResponseEntity containing a list of matching TransactionDTOs and HTTP status 200 (OK)
     * @throws IllegalArgumentException if the start date is after the end date
     */
    @GetMapping("/between")
    public ResponseEntity<List<TransactionDTO>> getTransactionsBetweenDates(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        logger.info("Fetching transactions between {} and {}", start, end);
        return ResponseEntity.ok(transactionService.findByCreatedAtBetween(start, end));
    }

    /**
     * Retrieves all transactions associated with a specific buyer.
     * <p>
     * This endpoint returns all transactions where the specified user is the buyer,
     * allowing users to view their purchase history.
     * </p>
     *
     * @param buyerId the unique identifier of the buyer
     * @return a ResponseEntity containing a list of matching TransactionDTOs and HTTP status 200 (OK)
     * @throws stud.ntnu.no.backend.user.exception.UserNotFoundException if no user with the given ID exists
     */
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByBuyerId(@PathVariable String buyerId) {
        logger.info("Fetching transactions for buyerId: {}", buyerId);
        return ResponseEntity.ok(transactionService.getTransactionsByBuyerId(buyerId));
    }

    /**
     * Processes a complete purchase transaction and updates the item availability.
     * <p>
     * This specialized endpoint handles the purchase flow, creating a transaction record
     * and marking the associated item as no longer available for purchase.
     * </p>
     *
     * @param dto the data transfer object containing transaction and purchase details
     * @return a ResponseEntity containing the created TransactionDTO and HTTP status 201 (Created)
     * @throws stud.ntnu.no.backend.user.exception.UserNotFoundException if the buyer or seller does not exist
     * @throws stud.ntnu.no.backend.item.exception.ItemNotFoundException if the item does not exist
     * @throws stud.ntnu.no.backend.item.exception.ItemNotAvailableException if the item is not available for purchase
     * @throws stud.ntnu.no.backend.transaction.exception.TransactionValidationException if the purchase violates business rules
     */
    @PostMapping("/purchase")
    public ResponseEntity<TransactionDTO> purchaseItem(@RequestBody CreateTransactionRequest dto) {
        logger.info("Processing purchase for item ID: {}", dto.getItemId());
        return new ResponseEntity<>(transactionService.handlePurchaseTransaction(dto), HttpStatus.CREATED);
    }
}
