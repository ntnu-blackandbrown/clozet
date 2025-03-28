// src/main/java/stud/ntnu/no/backend/transaction/exception/TransactionNotFoundException.java
package stud.ntnu.no.backend.transaction.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(Long id) {
        super("Transaction not found with id: " + id);
    }
    
    public TransactionNotFoundException(String message) {
        super(message);
    }
}