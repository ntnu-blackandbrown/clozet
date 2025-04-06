package stud.ntnu.no.backend.transaction.controller;

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
 * REST controller for managing transactions.
 * <p>
 * This controller provides endpoints for CRUD operations on transactions.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Constructs a new TransactionController with the specified service.
     *
     * @param transactionService the TransactionService
     */
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    
    /**
     * Retrieves all transactions.
     *
     * @return a list of TransactionDTOs
     */
    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }
    
    /**
     * Retrieves a transaction by its ID.
     *
     * @param id the ID of the transaction
     * @return the TransactionDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }
    
    /**
     * Creates a new transaction.
     *
     * @param dto the CreateTransactionRequest
     * @return the created TransactionDTO
     */
    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody CreateTransactionRequest dto) {
        return new ResponseEntity<>(transactionService.createTransaction(dto), HttpStatus.CREATED);
    }
    
    /**
     * Updates an existing transaction.
     *
     * @param id the ID of the transaction to update
     * @param dto the UpdateTransactionRequest with updated information
     * @return the updated TransactionDTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Long id, @RequestBody UpdateTransactionRequest dto) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, dto));
    }
    
    /**
     * Deletes a transaction by its ID.
     *
     * @param id the ID of the transaction to delete
     * @return a ResponseEntity with status 204 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves transactions created between the specified dates.
     *
     * @param start the start date and time
     * @param end the end date and time
     * @return a list of TransactionDTOs
     */
    @GetMapping("/between")
    public ResponseEntity<List<TransactionDTO>> getTransactionsBetweenDates(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(transactionService.findByCreatedAtBetween(start, end));
    }
}