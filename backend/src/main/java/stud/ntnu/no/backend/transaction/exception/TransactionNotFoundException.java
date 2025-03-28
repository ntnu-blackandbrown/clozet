package stud.ntnu.no.backend.transaction.exception;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(Long id) {
        super("Transaction not found with id: " + id);
    }
}