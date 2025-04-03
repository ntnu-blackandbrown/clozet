package stud.ntnu.no.backend.common.exception;

public class DataIntegrityException extends RuntimeException {
    public DataIntegrityException(String message) {
        super(message);
    }
    
    public DataIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }
}