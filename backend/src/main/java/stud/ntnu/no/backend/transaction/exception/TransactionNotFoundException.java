// src/main/java/stud/ntnu/no/backend/transaction/exception/TransactionNotFoundException.java
package stud.ntnu.no.backend.transaction.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a transaction is not found.
 * <p>
 * This exception is mapped to a 404 Not Found HTTP status code.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransactionNotFoundException extends RuntimeException {
    /**
     * Constructs a new TransactionNotFoundException with the specified transaction ID.
     *
     * @param id the ID of the transaction that was not found
     */
    public TransactionNotFoundException(Long id) {
        super("Transaction not found with id: " + id);
    }
    
    /**
     * Constructs a new TransactionNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public TransactionNotFoundException(String message) {
        super(message);
    }
}