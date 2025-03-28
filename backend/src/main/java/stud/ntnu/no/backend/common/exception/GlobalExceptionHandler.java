package stud.ntnu.no.backend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import stud.ntnu.no.backend.category.exception.CategoryNotFoundException;
import stud.ntnu.no.backend.category.exception.CategoryValidationException;
import stud.ntnu.no.backend.user.exception.EmailAlreadyInUseException;
import stud.ntnu.no.backend.user.exception.UserNotFoundException;
import stud.ntnu.no.backend.user.exception.UserValidationException;
import stud.ntnu.no.backend.user.exception.UsernameAlreadyExistsException;
import stud.ntnu.no.backend.review.exception.ReviewNotFoundException;
import stud.ntnu.no.backend.review.exception.ReviewValidationException;
import stud.ntnu.no.backend.shippingoption.exception.ShippingOptionNotFoundException;
import stud.ntnu.no.backend.shippingoption.exception.ShippingOptionValidationException;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Review exception handlers
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<Object> handleReviewNotFoundException(ReviewNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ReviewValidationException.class)
    public ResponseEntity<Object> handleReviewValidationException(ReviewValidationException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Category exception handlers
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Object> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoryValidationException.class)
    public ResponseEntity<Object> handleCategoryValidationException(CategoryValidationException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // User exception handlers
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<Object> handleUserValidationException(UserValidationException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<Object> handleEmailAlreadyInUseException(EmailAlreadyInUseException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Object> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // Generic exception handlers
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(Exception ex) {
        return buildErrorResponse("Unexpected error occurred: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }


    //Item class
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public class InvalidDataException extends RuntimeException {
        public InvalidDataException(String message) {
            super(message);
        }
    }

    // ShippingOption exception handlers
    @ExceptionHandler(ShippingOptionNotFoundException.class)
    public ResponseEntity<Object> handleShippingOptionNotFoundException(ShippingOptionNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ShippingOptionValidationException.class)
    public ResponseEntity<Object> handleShippingOptionValidationException(ShippingOptionValidationException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}